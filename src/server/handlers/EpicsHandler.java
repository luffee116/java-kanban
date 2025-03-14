package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length == 2) {
                    getEpics(exchange);
                } else if (path.length == 3) {
                    getEpicById(exchange);
                } else if (path.length == 4) {
                    getEpicSubtasks(exchange);
                }
            }
            case POST -> {
                if (path.length == 2) {
                    createEpic(exchange);
                } else if (path.length == 3) {
                    updateEpic(exchange);
                }
            }
            case DELETE -> deleteEpic(exchange);
        }
    }

    public void getEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(exchange, gson.toJson(epics), HttpURLConnection.HTTP_OK);
    }

    public void getEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Epic> optionalEpic = taskManager.getEpicById(id.get());

            if (optionalEpic.isPresent()) {
                sendText(exchange, gson.toJson(optionalEpic.get()), HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Epic с id " + id.get() + " не найден", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Неверный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isPresent()) {
            Optional<Epic> epicOptional = taskManager.getEpicById(id.get());
            if (epicOptional.isPresent()) {
                List<Subtask> subtasks = taskManager.getSubtasksOfEpic(id.get());
                if (!subtasks.isEmpty()) {
                    sendText(exchange, gson.toJson(subtasks), HttpURLConnection.HTTP_OK);
                } else {
                    sendText(exchange, "У указанного Epic'a нет Substask'ов", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                sendText(exchange, "Невозможно получить данные о подзадачах, так как Epic с id"
                        + id.get() + " не найден", HttpURLConnection.HTTP_NOT_FOUND);
            }
        }
        sendText(exchange, "Неверный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    public void createEpic(HttpExchange exchange) throws IOException {
        if (checkHeadersOfRequest(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            Optional<Epic> epicOptional = parseEpic(inputStream);
            Optional<Integer> id = getId(exchange);

            if (epicOptional.isPresent() && id.isEmpty()) {
                taskManager.createEpic(epicConverterWithoutId(epicOptional.get()));
                sendText(exchange, "Задача добавлена, присвоенный id = " + taskManager.getLastId(), HttpURLConnection.HTTP_CREATED);
            }
        }
        sendText(exchange, "Неверный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    public void deleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isPresent()) {
            if (taskManager.getEpicById(id.get()).isPresent()) {
                taskManager.removeEpicById(id.get());
                sendText(exchange, "Задача удалена", HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Задача с id " + id.get() + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Неверный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void updateEpic(HttpExchange exchange) throws IOException {
        if (checkHeadersOfRequest(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            Optional<Integer> id = getId(exchange);
            if (id.isPresent()) {
                if (taskManager.getEpicById(id.get()).isPresent()) {
                    Optional<Epic> updateEpic = parseEpic(inputStream);
                    if (updateEpic.isPresent()) {
                        taskManager.updateEpic(epicConverter(id.get(), updateEpic.get()));
                        sendText(exchange, "Задача обновлена", HttpURLConnection.HTTP_OK);
                    } else {
                        sendText(exchange, "Неверный формат эпика", HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                } else {
                    sendText(exchange, "Эпик с id = " + id.get() + " не найден", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                sendText(exchange, "Неверный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            sendText(exchange, "Неверный формат запроса", HttpURLConnection.HTTP_NOT_FOUND);
        }

    }

    public Epic epicConverter(Integer id, Epic epic) {
        return new Epic(id, epic.getTitle(), epic.getDescription());
    }

    public Epic epicConverterWithoutId(Epic epic) {
        return new Epic(epic.getTitle(), epic.getDescription());
    }

    public Optional<Epic> parseEpic(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epicOptional = gson.fromJson(jsonObject, Epic.class);

        return (epicOptional != null) ? Optional.of(epicOptional) : Optional.empty();
    }
}

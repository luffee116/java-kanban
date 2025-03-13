package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length == 3) {
                    getSubtaskById(exchange);
                } else {
                    getAllSubtasks(exchange);
                }
            }
            case POST -> {
                if (path.length == 3) {
                    updateSubtask(exchange);
                } else {
                    createSubtask(exchange);
                }
            }
            case DELETE -> removeSubtaskByID(exchange);
        }
    }

    public void getAllSubtasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), HttpURLConnection.HTTP_OK);
    }

    public void getSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Subtask> subtask = taskManager.getSubtaskById(id.get());
            if (subtask.isPresent()) {
                sendText(exchange, gson.toJson(subtask.get()), HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Задача с id " + id.get() + " отсутствует", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Неверный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createSubtask(HttpExchange httpExchange) throws IOException {
        if (checkHeadersOfRequest(httpExchange)) {
            InputStream inputStream = httpExchange.getRequestBody();
            Optional<Subtask> subtask = parseSubtask(inputStream);

            Optional<Integer> id = getId(httpExchange);

            if (id.isEmpty()) {
                if (subtask.isPresent()) {
                    if (subtask.get().getEpicID() != 0) {
                        try {
                            taskManager.createSubtask(subtask.get());
                            sendText(httpExchange, "Задача добавлена", HttpURLConnection.HTTP_CREATED);
                        } catch (Exception e) {
                            sendText(httpExchange, "Задача пересекается с другой", HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                        }
                    } else {
                        sendText(httpExchange, "Эпика с id " + subtask.get().getEpicID() + " не существует", HttpURLConnection.HTTP_NOT_FOUND);
                    }
                } else {
                    sendText(httpExchange, "Неправильный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            }
        }
    }

    private void updateSubtask(HttpExchange exchange) throws IOException {
        if (checkHeadersOfRequest(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            Optional<Integer> id = getId(exchange);
            Optional<Subtask> subtask = parseSubtask(inputStream);
            if (id.isPresent()) {
                if (subtask.isPresent()) {
                    if (taskManager.getSubtaskById(id.get()).isPresent()) {
                        if (taskManager.getEpicById(subtask.get().getEpicID()).isPresent()) {
                            try {
                                taskManager.updateSubtask(subtaskConverter(subtask.get(), id.get()));
                                sendText(exchange, "Задача обновлена", HttpURLConnection.HTTP_CREATED);
                            } catch (Exception e) {
                                sendText(exchange, "Задача пересекается с другой", HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                            }
                        } else {
                            sendText(exchange, "Невозможно обновить задачу c EpicID = " + subtask.get().getEpicID(), HttpURLConnection.HTTP_NOT_FOUND);
                        }
                    } else {
                        sendText(exchange, "Задача с id " + id.get() + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
                    }
                }
            } else {
                sendText(exchange, "Неправильный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            sendText(exchange, "Неправильный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private void removeSubtaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            if (taskManager.getSubtaskById(id.get()).isPresent()) {
                taskManager.removeSubtaskById(id.get());
                sendText(exchange, "Задача с id " + id.get() + " удалена", HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Задачи с id " + id.get() + " не найдено", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Неправильный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private Optional<Subtask> parseSubtask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);

        return (subtask != null) ? Optional.of(subtask) : Optional.empty();
    }

    private Subtask subtaskConverter(Subtask subtask, int id) {
        return new Subtask(id,
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getEpicID(),
                subtask.getDuration(),
                subtask.getTimeStart());
    }
}

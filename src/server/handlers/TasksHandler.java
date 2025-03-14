package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {


    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length == 3) {
                    getTaskById(exchange);
                } else {
                    getAllTasks(exchange);
                }
            }
            case POST -> {
                if (path.length == 3) {
                    updateTask(exchange);
                } else {
                    createTask(exchange);
                }
            }
            case DELETE -> deleteTaskById(exchange);
        }

    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(id.get());
            if (task.isPresent()) {
                sendText(exchange, gson.toJson(task.get()), HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Задача с id " + id.get() + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Указан неверный формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getAllTasks()), HttpURLConnection.HTTP_OK);
    }

    private void createTask(HttpExchange exchange) throws IOException {
        if (checkHeadersOfRequest(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            Optional<Task> task = parseTask(inputStream);
            Optional<Integer> id = getId(exchange);

            if (id.isEmpty()) {
                if (task.isPresent()) {
                    try {
                        taskManager.createTask(task.get());
                        sendText(exchange, "Задача добавлена, присвоенный id = " + taskManager.getLastId() , HttpURLConnection.HTTP_CREATED);
                    } catch (RuntimeException e) {
                        sendText(exchange, "Задача пересекается с другой", HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                }
            } else {
                sendText(exchange, "Неправильный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        }
    }

    private Optional<Task> parseTask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);

        return (task != null) ? Optional.of(task) : Optional.empty();
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        if (checkHeadersOfRequest(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            Optional<Task> task = parseTask(inputStream);

            Optional<Integer> id = getId(exchange);
            if (id.isPresent()) {
                Optional<Task> taskToUpdate = taskManager.getTaskById(id.get());
                if (task.isPresent() && taskToUpdate.isPresent()) {
                    try {
                        taskManager.updateTask(taskConverter(task.get(), id.get()));
                        sendText(exchange, "Задача успешно обновлена", HttpURLConnection.HTTP_OK);
                    } catch (Exception e) {
                        sendText(exchange, "Задача пересекается с другой", HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                } else {
                    sendText(exchange, "Задача с id " + id.get() + " не найдена", HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            } else {
                sendText(exchange, "Неверно указан формат id", HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            sendText(exchange, "Неверный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void deleteTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(id.get());
            if (task.isPresent()) {
                taskManager.removeTaskById(id.get());
                sendText(exchange, "Задача с id " + id.get() + " удалена", HttpURLConnection.HTTP_OK);
            } else {
                sendText(exchange, "Задача с id " + id.get() + " не найдена", HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendText(exchange, "Неверно указан id", HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public Task taskConverter(Task task, int id) {
        return new Task(id,
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDuration(),
                task.getTimeStart());
    }

}

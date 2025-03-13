package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import manager.TaskManager;
import server.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (httpMethod.equals(HttpMethod.GET)) {
            if (path.length == 2) {
                getPrioritizedTasks(exchange);
            } else {
                sendText(exchange, "Неправильный формат запроса", HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        }
    }

    public void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTaskByPriority()), HttpURLConnection.HTTP_OK);
    }


}

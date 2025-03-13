package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes());
        }
    }

    protected Optional<Integer> getId(HttpExchange httpExchange) {
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected boolean checkHeadersOfRequest(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        List<String> contentType = headers.get("Content-type");
        return (contentType != null) && (contentType.contains("application/json"));
    }
}

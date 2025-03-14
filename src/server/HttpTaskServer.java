package server;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int port;
    private final String hostname;
    private final HttpServer httpServer;

    public HttpTaskServer(int port, String host, TaskManager taskManager) throws IOException {
        this.port = port;
        this.hostname = host;

        httpServer = HttpServer.create(new InetSocketAddress(hostname, port), 0);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        System.out.printf("Сервер доступен по адресу http://%s:%d\n", hostname, port);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен");
        httpServer.stop(1);
    }
}

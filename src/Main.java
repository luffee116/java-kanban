import manager.Manager;
import server.HttpTaskServer;

import java.io.IOException;

public class Main {
    private static final int port = 8080;
    private static final String host = "localhost";

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(port, host, Manager.getDefault());
        // Запуск сервера
        httpTaskServer.start();

        // Остановка сервера
        // httpTaskServer.stop();
    }
}
package HttpTaskServerTest;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Manager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServerTest {
    private final TaskManager taskManager = Manager.getDefault();
    private final HttpTaskServer server = new HttpTaskServer(8080, "localhost", taskManager);
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
        server.start();
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test",
                "Test description",
                Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 12, 10, 0));

        String request = gson.toJson(task);

        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 201;
        int expectedTaskSize = 1;
        String expectedTaskName = "Test";
        int actualCode = httpResponse.statusCode();
        int actualTaskSize = taskManager.getAllTasks().size();
        String actualTaskName = taskManager.getAllTasks().getFirst().getTitle();

        Assertions.assertEquals(expectedCode, actualCode, "Верный код ответа - 201");
        Assertions.assertEquals(expectedTaskSize, actualTaskSize, "Ошибка в подсчете количества задач");
        Assertions.assertEquals(expectedTaskName, actualTaskName, "Ошибка в создании Task");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test",
                "Test desc",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 12, 10, 0));

        String firstRequest = gson.toJson(task);

        HttpRequest httpRequest1 = HttpRequest
                .newBuilder()
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(firstRequest))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        httpClient.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("UPDATED",
                "Test desc",
                Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 12, 10, 0));

        String secondRequest = gson.toJson(task2);

        HttpRequest httpRequest2 = HttpRequest
                .newBuilder()
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(secondRequest))
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .build();

        HttpResponse<String> httpResponse2 = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int expectedSize = 1;
        String expectedTitle = "UPDATED";
        Status expectedStatus = Status.DONE;

        int actualCode = httpResponse2.statusCode();
        int actualSize = taskManager.getAllTasks().size();
        String actualTitle = taskManager.getAllTasks().getFirst().getTitle();
        Status actualStatus = taskManager.getAllTasks().getFirst().getStatus();

        Assertions.assertEquals(expectedCode, actualCode);
        Assertions.assertEquals(expectedSize, actualSize);
        Assertions.assertEquals(expectedTitle, actualTitle);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Test",
                "Test desc",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 12, 10, 0));

        String request = gson.toJson(task);

        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();

        httpClient.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> httpResponse2 = httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = httpResponse2.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);

        String actualBody = httpResponse2.body();
        String expectedBody = "[{\"id\":1,\"title\":\"Test\",\"description\":\"Test desc\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"10:00:00/12.03.2025\"}]";
        Assertions.assertEquals(expectedBody, actualBody);
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("TEST",
                "TEST",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));

        String request = gson.toJson(task1);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = getResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);

        String expectedBody = "{\"id\":1,\"title\":\"TEST\",\"description\":\"TEST\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"12:12:00/12.12.2025\"}";
        String actualBody = getResponse.body();
        Assertions.assertEquals(expectedBody, actualBody);
    }

    @Test
    public void testDeleteTaskByID() throws IOException, InterruptedException {
        Task task = new Task("TEST",
                "test",
                Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));

        String gsonRequest = gson.toJson(task);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gsonRequest))
                .headers("Content-Type", "application/json")
                .build();

        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .build();

        HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = deleteResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);

        int expectedSize = 0;
        int actualSize = taskManager.getAllTasks().size();
        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "TEST desc");

        String request = gson.toJson(epic);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 201;
        int actualCode = postResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);

        int expectedSize = 1;
        int actualSize = taskManager.getAllEpics().size();
        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "TEST desc");

        String request = gson.toJson(epic);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = getResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);

        String expectedBode = "[{\"subtasksId\":[],\"timeEnd\":null,\"id\":1,\"title\":\"TEST\",\"description\":\"TEST desc\",\"status\":\"NEW\",\"duration\":null,\"timeStart\":null}]";
        String actualBody = getResponse.body();
        Assertions.assertEquals(expectedBode, actualBody);
    }

    @Test
    public void testGetEpicByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request = gson.toJson(epic);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        int actualCode = getResponse.statusCode();
        int expectedCode = 200;
        Assertions.assertEquals(expectedCode, actualCode);

        String actualBody = getResponse.body();
        String expectedResponse = "{\"subtasksId\":[],\"timeEnd\":null,\"id\":1,\"title\":\"TEST\",\"description\":\"test\",\"status\":\"NEW\",\"duration\":null,\"timeStart\":null}";
        Assertions.assertEquals(expectedResponse, actualBody);
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request = gson.toJson(epic);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/1")).build();

        HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        int actualCode = deleteResponse.statusCode();
        int expectedCode = 200;
        Assertions.assertEquals(expectedCode, actualCode);

        int actualSize = taskManager.getAllEpics().size();
        int expectedSize = 0;
        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic = new Epic("TEST", "test");
        String request = gson.toJson(epic);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();

        httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        // Создаем сабтаск
        Subtask subtask = new Subtask("TEST",
                "TEST", Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));

        String request2 = gson.toJson(subtask);

        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();

        httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        // Создаем GET запрос
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        int actualCode = getResponse.statusCode();
        int expectedCode = 200;
        Assertions.assertEquals(expectedCode, actualCode);

        String actualSubtasks = getResponse.body();
        String expectedSubtasks = "[{\"epicID\":1,\"id\":2,\"title\":\"TEST\",\"description\":\"TEST\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"12:12:00/12.12.2025\"}]";
        Assertions.assertEquals(expectedSubtasks, actualSubtasks);
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");

        String request1 = gson.toJson(epic);

        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request1))
                .build();

        httpClient.send(postRequest1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TEST",
                "test",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));

        String request2 = gson.toJson(subtask);

        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();

        HttpResponse<String> response2 = httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 201;
        int actualCode = response2.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        int expectedSize = 1;
        int actualSize = taskManager.getAllSubtasks().size();
        Assertions.assertEquals(expectedSize, actualSize);
    }


    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request1 = gson.toJson(epic);
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request1))
                .build();
        httpClient.send(postRequest1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TEST",
                "test",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        String request2 = gson.toJson(subtask);
        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();
        httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask(
                "TEST",
                "TEST",
                Status.DONE,
                1,
                Duration.ofMinutes(20),
                LocalDateTime.of(2025, 12, 12, 12, 13));
        String request3 = gson.toJson(subtask2);
        HttpRequest postRequest3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request3))
                .build();
        HttpResponse<String> response3 = httpClient.send(postRequest3, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 201;
        int actualCode = response3.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        int expectedSize = 1;
        int actualSize = taskManager.getAllSubtasks().size();
        Assertions.assertEquals(expectedSize, actualSize);
        Status expectedStatus = Status.DONE;
        Status actualStatus = taskManager.getAllSubtasks().getFirst().getStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    public void testDeleteSubtaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request1 = gson.toJson(epic);
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request1))
                .build();
        httpClient.send(postRequest1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TEST",
                "test",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        String request2 = gson.toJson(subtask);
        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();
        httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = deleteResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        int expectedSize = 0;
        int actualSize = taskManager.getAllSubtasks().size();
        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request1 = gson.toJson(epic);
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request1))
                .build();
        httpClient.send(postRequest1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TEST",
                "test",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        String request2 = gson.toJson(subtask);
        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();
        httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("SECOND",
                "SECOND",
                Status.DONE,
                1,
                Duration.ofMinutes(60),
                LocalDateTime.of(2025, 12, 10, 12, 12));
        String request3 = gson.toJson(subtask2);
        HttpRequest postRequest3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request3))
                .build();
        httpClient.send(postRequest3, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET().
                build();
        HttpResponse<String> response4 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response4.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        String expectedBody = "[{\"epicID\":1,\"id\":2,\"title\":\"TEST\",\"description\":\"test\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"12:12:00/12.12.2025\"},{\"epicID\":1,\"id\":3,\"title\":\"SECOND\",\"description\":\"SECOND\",\"status\":\"DONE\",\"duration\":60,\"timeStart\":\"12:12:00/10.12.2025\"}]";
        String actualBody = response4.body();
        Assertions.assertEquals(expectedBody, actualBody);
    }

    @Test
    public void testGetSubtasksByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TEST", "test");
        String request1 = gson.toJson(epic);
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request1))
                .build();
        httpClient.send(postRequest1, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("TEST",
                "test",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 12, 12, 12, 12));
        String request2 = gson.toJson(subtask);
        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();
        httpClient.send(postRequest2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getByIDRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();
        HttpResponse<String> getResponse = httpClient.send(getByIDRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = getResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        String expectedBody = "{\"epicID\":1,\"id\":2,\"title\":\"TEST\",\"description\":\"test\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"12:12:00/12.12.2025\"}";
        String actualBody = getResponse.body();
        Assertions.assertEquals(expectedBody, actualBody);
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(
                "TEST",
                "test",
                Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 12, 10, 0));
        String request = gson.toJson(task1);
        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();
        httpClient.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();
        httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());

        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> historyResponse = httpClient.send(historyRequest, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = historyResponse.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        String expectedBody = "[{\"id\":1,\"title\":\"TEST\",\"description\":\"test\",\"status\":\"DONE\",\"duration\":10,\"timeStart\":\"10:00:00/12.03.2025\"}]";
        String actualBody = historyResponse.body();
        Assertions.assertEquals(expectedBody, actualBody);
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task(
                "FIRST",
                "FIRST",
                Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2026, 3, 12, 10, 0));
        String request = gson.toJson(task1);
        HttpRequest httpRequest1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request))
                .build();
        httpClient.send(httpRequest1, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(
                "SECOND",
                "SECOND",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 14, 10, 0));
        String request2 = gson.toJson(task2);
        HttpRequest httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request2))
                .build();
        httpClient.send(httpRequest2, HttpResponse.BodyHandlers.ofString());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        int expectedCode = 200;
        int actualCode = response3.statusCode();
        Assertions.assertEquals(expectedCode, actualCode);
        String expectedBody = "[{\"id\":2,\"title\":\"SECOND\",\"description\":\"SECOND\",\"status\":\"NEW\",\"duration\":10,\"timeStart\":\"10:00:00/14.03.2025\"},{\"id\":1,\"title\":\"FIRST\",\"description\":\"FIRST\",\"status\":\"DONE\",\"duration\":10,\"timeStart\":\"10:00:00/12.03.2026\"}]";
        String actualBody = response3.body();
        Assertions.assertEquals(expectedBody, actualBody);
    }
}

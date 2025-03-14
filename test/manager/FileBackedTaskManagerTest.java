package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class FileBackedTaskManagerTest {
    private Path path;
    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    @BeforeEach
    void preparation() {
        try {
            path = Files.createTempFile("data", ".cvs");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = Manager.getFileBackedTaskManager(path.toFile());

        task1 = new Task(
                "Помыть посуду",
                "Просушить",
                Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 10, 0));
        task2 = new Task(
                "Покормить собаку",
                "Сухой корм",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 11, 0));
        epic1 = new Epic(
                "Уборка дома",
                "Генеральная");
        epic2 = new Epic(
                "Химчистка машины",
                "ул. Бухарская д.25");
        subtask1 = new Subtask(
                5,
                "Помыть полы",
                "Сухая и влажная уборка",
                Status.IN_PROGRESS,
                3,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 12, 0));
        subtask2 = new Subtask(
                6,
                "Помыть окна",
                "Использовать химию",
                Status.NEW,
                3,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 13, 0));
        subtask3 = new Subtask(
                7,
                "Пропылесосить коврики",
                "Заплатить мастеру",
                Status.DONE,
                4,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 14, 0));
    }

    @Test
    void testLoad() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        TaskManager loadedFromFile = Manager.loadFromFile(path.toFile());
        String expected = taskManager.getAllTasks() + " " + taskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        String actual = loadedFromFile.getAllTasks() + " " + loadedFromFile.getAllEpics() + " " + loadedFromFile.getAllSubtasks();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testAddToFile() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        TaskManager loadedFromFile = Manager.loadFromFile(path.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedFromFile.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task1);
        Optional<Task> task = taskManager.getTaskById(1);
        task.ifPresent(value -> {
            value.setTitle("test");
            taskManager.updateTask(value);
        });
        TaskManager loadedFromFile = Manager.loadFromFile(path.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedFromFile.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateSubtask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        Optional<Subtask> tmp = taskManager.getSubtaskById(subtask1.getId());
        tmp.ifPresent(subtask -> {
            subtask.setTitle("test");
            taskManager.updateSubtask(subtask);
        });
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllSubtasks().toString(), loaded.getAllSubtasks().toString());
    }

    @Test
    void testUpdateEpic() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        Optional<Epic> tmp = taskManager.getEpicById(epic1.getId());
        tmp.ifPresent(epic -> taskManager.updateEpic(epic));
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllEpics().toString(), loaded.getAllEpics().toString());
        Assertions.assertEquals(taskManager.getAllSubtasks().toString(), loaded.getAllSubtasks().toString());
    }

    @Test
    void testDeleteTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(task1.getId());
        TaskManager loadedFromFile = Manager.loadFromFile(path.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedFromFile.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testDeleteSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeSubtaskById(subtask1.getId());
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllSubtasks(), loaded.getAllSubtasks());
    }

    @Test
    void testDeleteEpicByID() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(epic1.getId());
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllEpics(), loaded.getAllEpics());
    }

    @Test
    void testDeleteAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllTasks(), loaded.getAllTasks());
    }

    @Test
    void testDeleteAllSubtasks() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.removeAllSubtasks();
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllSubtasks(), loaded.getAllSubtasks());
    }

    @Test
    void testDeleteAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask3);
        taskManager.removeAllEpics();
        TaskManager loaded = Manager.loadFromFile(path.toFile());
        Assertions.assertEquals(taskManager.getAllEpics(), loaded.getAllEpics());
        Assertions.assertEquals(taskManager.getAllSubtasks(), loaded.getAllSubtasks());
    }

}

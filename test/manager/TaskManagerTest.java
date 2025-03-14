package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createManager();

    protected TaskManager taskManager;
    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected Epic epic2;

    @BeforeEach
    public void init() {
        taskManager = Manager.getDefault();
        epic1 = new Epic("First",
                "First",
                Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 26, 8, 0));
        epic2 = new Epic("Second",
                "Second",
                Duration.ofHours(1),
                LocalDateTime.of(2025, 2, 26, 10, 0));
        task1 = new Task("Хлеб",
                "Купить",
                Status.NEW, Duration.ofMinutes(20),
                LocalDateTime.of(2025, 2, 25, 13, 30));
        task2 = new Task("Помыть",
                "Машину", Status.DONE,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 25, 14, 0));
    }

    @Test
    public void testCreateTask() {
        taskManager.createTask(task1);
        String expected = "[Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=20, timeStart=13:30:00/25.02.2025, timeEnd=13:50:00/25.02.2025}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testCreateSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtaskTest = new Subtask("Test", "test", Status.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2002, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest);
        String expected = "[Subtask{epicID=1, id=2, title='Test', description='test', status=NEW, duration=10, timeStart=01:01:00/01.01.2002, timeEnd=01:11:00/01.01.2002}]";
        String actually = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testCreateEpic() {
        taskManager.createEpic(epic1);
        String actually = taskManager.getAllEpics().toString();
        String expected = "[Epic{ id=1, title='First', description='First', status=NEW, duration=60, timeStart=08:00:00/26.02.2025, timeEnd=09:00:00/26.02.2025}]";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetEpic() {
        taskManager.createEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        String actually = taskManager.getAllEpics().toString();
        String expected = "[Epic{ id=1, title='First', description='First', status=NEW, duration=60, timeStart=08:00:00/26.02.2025, timeEnd=09:00:00/26.02.2025}]";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtaskTest = new Subtask("Test", "test", Status.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2002, 2, 2, 2, 2));
        taskManager.createSubtask(subtaskTest);
        String actually = taskManager.getSubtaskById(subtaskTest.getId()).get().toString();
        String expected = "Subtask{epicID=1, id=2, title='Test', description='test', status=NEW, duration=10, timeStart=02:02:00/02.02.2002, timeEnd=02:12:00/02.02.2002}";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetTask() {
        taskManager.createTask(task1);
        String actually = taskManager.getTaskById(task1.getId()).get().toString();
        String expected = "Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=20, timeStart=13:30:00/25.02.2025, timeEnd=13:50:00/25.02.2025}";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testUpdateTask() {
        taskManager.createTask(task1);
        String expected = "Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=9, timeStart=01:01:00/01.01.2025, timeEnd=01:10:00/01.01.2025}";
        Task task = new Task(1, "Хлеб", "Купить", Status.NEW, Duration.ofMinutes(9), LocalDateTime.of(2025, 1, 1, 1, 1));
        taskManager.updateTask(task);
        String actually = taskManager.getTaskById(1).get().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTaskById(task1.getId());
        String actual = taskManager.getAllTasks().toString();
        String expected = "[Task{id=2, title='Помыть', description='Машину', status=DONE, duration=10, timeStart=14:00:00/25.02.2025, timeEnd=14:10:00/25.02.2025}]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();
        String expected = "[]";
        String actual = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=20, timeStart=13:30:00/25.02.2025, timeEnd=13:50:00/25.02.2025}, Task{id=2, title='Помыть', description='Машину', status=DONE, duration=10, timeStart=14:00:00/25.02.2025, timeEnd=14:10:00/25.02.2025}]";
        String actual = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String actual = taskManager.getAllEpics().toString();
        String expected = "[Epic{ id=1, title='First', description='First', status=NEW, duration=60, timeStart=08:00:00/26.02.2025, timeEnd=09:00:00/26.02.2025}, Epic{ id=2, title='Second', description='Second', status=NEW, duration=60, timeStart=10:00:00/26.02.2025, timeEnd=11:00:00/26.02.2025}]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUpdateEpic() {
        taskManager.createEpic(epic1);
        Optional<Epic> epic = taskManager.getEpicById(epic1.getId());
        epic.ifPresent(tmp -> {
            tmp.setTitle("Test");
            taskManager.updateEpic(tmp);
        });
        String expected = "[Epic{ id=1, title='Test', description='First', status=NEW, duration=60, timeStart=08:00:00/26.02.2025, timeEnd=09:00:00/26.02.2025}]";
        String actual = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(epic1.getId());
        String expected = "[Epic{ id=2, title='Second', description='Second', status=NEW, duration=60, timeStart=10:00:00/26.02.2025, timeEnd=11:00:00/26.02.2025}]";
        String actual = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();
        String expected = "[]";
        String actual = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUpdateSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("First", "First", Status.NEW, epic1.getId(), Duration.ofMinutes(20), LocalDateTime.of(2025, 10, 20, 1, 1));
        taskManager.createSubtask(subtask);
        Optional<Subtask> subtask1 = taskManager.getSubtaskById(subtask.getId());
        subtask1.ifPresent(sub -> sub.setTitle("Test"));
        String expected = "[Subtask{epicID=1, id=2, title='Test', description='First', status=NEW, duration=20, timeStart=01:01:00/20.10.2025, timeEnd=01:21:00/20.10.2025}]";
        String actual = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteSubtaskById() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("First", "First", Status.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2025, 10, 20, 10, 10));
        taskManager.createSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        String actual = taskManager.getAllSubtasks().toString();
        String expected = "[]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveAllSubtasks() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("First", "First", Status.NEW, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2025, 10, 10, 20, 10));
        Subtask subtask2 = new Subtask("Second", "Second", Status.DONE, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2025, 10, 21, 2, 2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeAllSubtasks();
        String expected = "[]";
        String actual = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        final int historySize = 2;
        Assertions.assertEquals(historySize, taskManager.getHistory().size());
        String expected = "[Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=20, timeStart=13:30:00/25.02.2025, timeEnd=13:50:00/25.02.2025}, Task{id=2, title='Помыть', description='Машину', status=DONE, duration=10, timeStart=14:00:00/25.02.2025, timeEnd=14:10:00/25.02.2025}]";
        String actual = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testPrioritizedTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[Task{id=1, title='Хлеб', description='Купить', status=NEW, duration=20, timeStart=13:30:00/25.02.2025, timeEnd=13:50:00/25.02.2025}, Task{id=2, title='Помыть', description='Машину', status=DONE, duration=10, timeStart=14:00:00/25.02.2025, timeEnd=14:10:00/25.02.2025}]";
        String actually = taskManager.getTaskByPriority().toString();
        Assertions.assertEquals(expected, actually);
    }


}

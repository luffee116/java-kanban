package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Epic epic1;
    private Epic epic2;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void init() {
        taskManager = Manager.getDefault();
        epic1 = new Epic("First", "First");
        epic2 = new Epic("Second", "Second");
        task1 = new Task("Хлеб", "Купить", Status.NEW);
        task2 = new Task("Помыть", "Машину", Status.DONE);
    }

    @Test
    public void testCreateTask() {
        taskManager.createTask(task1);
        String expected = "[model.Task {Название = 'Хлеб', Описание ='Купить', id =1, Статус =NEW}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testCreateSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtaskTest = new Subtask("Test", "test", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtaskTest);
        String expected = "[model.Subtask {, Название = 'Test', Описание = 'test', model.Epic Id = 1, Статус = NEW}]";
        String actually = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testCreateEpic() {
        taskManager.createEpic(epic1);
        String actually = taskManager.getAllEpics().toString();
        String expected = "[model.Epic{, Название = 'First', Описание = 'First' Статус = NEW, SubtasksId = []}]";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetEpic() {
        taskManager.createEpic(epic1);
        taskManager.getEpicById(epic1.getId());
        String actually = taskManager.getAllEpics().toString();
        String expected = "[model.Epic{, Название = 'First', Описание = 'First' Статус = NEW, SubtasksId = []}]";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetSubtask() {
        taskManager.createEpic(epic1);
        Subtask subtaskTest = new Subtask("Test", "test", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtaskTest);
        String actually = taskManager.getSubtaskById(subtaskTest.getId()).toString();
        String expected = "model.Subtask {, Название = 'Test', Описание = 'test', model.Epic Id = 1, Статус = NEW}";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testGetTask() {
        taskManager.createTask(task1);
        String actually = taskManager.getTaskById(task1.getId()).toString();
        String expected = "model.Task {Название = 'Хлеб', Описание ='Купить', id =1, Статус =NEW}";
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testUpdateTask() {
        taskManager.createTask(task1);
        String expected = "model.Task {Название = 'Хлеб', Описание ='Купить', id =1, Статус =NEW}";
        Task task = new Task(1, "Хлеб", "Купить", Status.NEW);
        taskManager.updateTask(task);
        String actually = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    public void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTaskById(task1.getId());
        String actual = taskManager.getAllTasks().toString();
        String expected = "[model.Task {Название = 'Помыть', Описание ='Машину', id =2, Статус =DONE}]";
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
        String expected = "[model.Task {Название = 'Хлеб', Описание ='Купить', id =1, Статус =NEW}, " +
                "model.Task {Название = 'Помыть', Описание ='Машину', id =2, Статус =DONE}]";
        String actual = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String actual = taskManager.getAllEpics().toString();
        String expected = "[model.Epic{, Название = 'First', Описание = 'First' Статус = NEW, SubtasksId = []}, " +
                "model.Epic{, Название = 'Second', Описание = 'Second' Статус = NEW, SubtasksId = []}]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUpdateEpic() {
        taskManager.createEpic(epic1);
        Epic epic = taskManager.getEpicById(epic1.getId());
        epic.setTitle("Test");
        taskManager.updateEpic(epic);
        String expected = "[model.Epic{, Название = 'Test', Описание = 'First' Статус = NEW, SubtasksId = []}]";
        String actual = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(epic1.getId());
        String expected = "[model.Epic{, Название = 'Second', Описание = 'Second' Статус = NEW, SubtasksId = []}]";
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

        Subtask subtask = new Subtask("First", "First", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask1 = taskManager.getSubtaskById(subtask.getId());
        subtask1.setTitle("Test");
        String expected = "[model.Subtask {, Название = 'Test', Описание = 'First', model.Epic Id = 1, Статус = NEW}]";
        String actual = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void testDeleteSubtaskById() {
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("First", "First", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        String actual = taskManager.getAllSubtasks().toString();
        String expected = "[]";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRemoveAllSubtasks() {
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("First", "First", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Second", "Second", Status.DONE, epic1.getId());
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
        String expected = "[model.Task {Название = 'Хлеб', Описание ='Купить', id =1, Статус =NEW}, " +
                "model.Task {Название = 'Помыть', Описание ='Машину', id =2, Статус =DONE}]";
        String actual = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testHistoryShouldNotExceedTenEntries() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(new Subtask("Title", "Description", Status.NEW, epic1.getId()));
        taskManager.createSubtask(new Subtask("Test", "Test", Status.DONE, epic2.getId()));

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
    }


}

package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void init() {
        historyManager = Manager.getDefaultHistory();
        task1 = new Task(1, "Молоко", "Купить", Status.NEW);
        task2 = new Task(2, "Машина", "Помыть", Status.IN_PROGRESS);
        epic1 = new Epic(3, "Домашка", "Выполнить", Status.NEW);
        epic2 = new Epic(4, "Уборка", "Убраться дома", Status.NEW);
        subtask1 = new Subtask(5, "Доделать спринт", "Быстро", Status.IN_PROGRESS, 3);
        subtask2 = new Subtask(6, "Помыть полы", "До прихода гостей", Status.DONE, 4);
    }

    @Test
    public void testAddTaskAndGetTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subtask1);
        historyManager.add(subtask2);

        String expected = "[model.Task {Название = 'Молоко', Описание ='Купить', id =1, Статус =NEW}, " +
                "model.Task {Название = 'Машина', Описание ='Помыть', id =2, Статус =IN_PROGRESS}, " +
                "model.Epic{, Название = 'Домашка', Описание = 'Выполнить' Статус = NEW, SubtasksId = []}, " +
                "model.Epic{, Название = 'Уборка', Описание = 'Убраться дома' Статус = NEW, SubtasksId = []}, " +
                "model.Subtask {, Название = 'Доделать спринт', Описание = 'Быстро', model.Epic Id = 3, Статус = IN_PROGRESS}, " +
                "model.Subtask {, Название = 'Помыть полы', Описание = 'До прихода гостей', model.Epic Id = 4, Статус = DONE}]";
        String actual = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testAddNullTasks() {
        Task task = null;
        Subtask subtask = null;
        Epic epic = null;
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    public void sizeOfStoriesShouldNotExceedSixElements() {
        final int maxSize = 6;

        historyManager.add(task1); //1
        historyManager.add(task2); //2
        historyManager.add(subtask1); //3
        historyManager.add(subtask2); //4
        historyManager.add(epic1);//5
        historyManager.add(epic2); //6
        historyManager.add(task1); //1
        historyManager.add(task2); //2
        historyManager.add(subtask1); //3
        historyManager.add(subtask2); //4
        Assertions.assertEquals(maxSize, historyManager.getHistory().size());
    }


}

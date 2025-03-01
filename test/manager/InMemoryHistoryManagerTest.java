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
        task1 = new Task(1, "Молоко", "Купить", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2002, 1, 1, 1, 1));
        task2 = new Task(2, "Машина", "Помыть", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2002, 2, 2, 2, 2));
        epic1 = new Epic(3, "Домашка", "Выполнить", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(1, 1, 1, 1, 1));
        epic2 = new Epic(4, "Уборка", "Убраться дома", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(1, 1, 1, 2, 1));
        subtask1 = new Subtask(5, "Доделать спринт", "Быстро", Status.IN_PROGRESS, 3, Duration.ofMinutes(10), LocalDateTime.of(2002, 3, 3, 3, 3));
        subtask2 = new Subtask(6, "Помыть полы", "До прихода гостей", Status.DONE, 4, Duration.ofMinutes(10), LocalDateTime.of(2002, 4, 4, 4, 4));
    }

    @Test
    public void testAddTaskAndGetTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        String expected = "[Task{id=1, title='Молоко', description='Купить', status=NEW, duration=10, timeStart=01:01:00/01.01.2002, timeEnd=01:11:00/01.01.2002}, Task{id=2, title='Машина', description='Помыть', status=IN_PROGRESS, duration=10, timeStart=02:02:00/02.02.2002, timeEnd=02:12:00/02.02.2002}, Epic{ id=3, title='Домашка', description='Выполнить', status=NEW, duration=10, timeStart=01:01:00/01.01.0001, timeEnd=01:11:00/01.01.0001}, Epic{ id=4, title='Уборка', description='Убраться дома', status=NEW, duration=10, timeStart=02:01:00/01.01.0001, timeEnd=02:11:00/01.01.0001}, Subtask{epicID=3, id=5, title='Доделать спринт', description='Быстро', status=IN_PROGRESS, duration=10, timeStart=03:03:00/03.03.2002, timeEnd=03:13:00/03.03.2002}, Subtask{epicID=4, id=6, title='Помыть полы', description='До прихода гостей', status=DONE, duration=10, timeStart=04:04:00/04.04.2002, timeEnd=04:14:00/04.04.2002}]";
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

import manager.Manager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("data.csv");
        TaskManager taskManager = Manager.getFileBackedTaskManager(file);

        //Создали 2 задачи, 1 эпик с 2 сабтасками, 1 эпик без сабтасков
        Task task1 = new Task(
                "Отработать понедельник",
                "Желательно живым",
                Status.NEW,
                Duration.ofHours(8),
                LocalDateTime.of(2024, 12, 22, 9, 20));

        Task task2 = new Task(
                "Купить корм кошке",
                "Без курицы",
                Status.DONE,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 12, 23, 10, 0));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Обед",
                "приготовить обед"
        );
        Epic epic2 = new Epic(
                "Универ",
                "Выполнить домашку"
        );

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subTask1 = new Subtask(
                "Макароны",
                "Отварить",
                Status.NEW,
                epic1.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 12, 24, 10, 0));

        Subtask subTask2 = new Subtask(
                "Чай",
                "Заварить",
                Status.IN_PROGRESS,
                epic1.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 12, 24, 10, 11));

        Subtask subTask3 = new Subtask(
                "Покушать",
                "Ужин",
                Status.IN_PROGRESS,
                epic1.getId(),
                Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 25, 10, 0));

        taskManager.createSubtask(subTask1);
        taskManager.createSubtask(subTask2);
        taskManager.createSubtask(subTask3);


        System.out.println("taskManager:");
        printAllTasks(taskManager);

        TaskManager taskManagerFromFile = Manager.loadFromFile(file);
        System.out.println("\ntaskManagerFromFile:");
        printAllTasks(taskManagerFromFile);
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЭпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПодзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}
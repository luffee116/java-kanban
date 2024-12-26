import manager.Manager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();

        // Создать несколько задач разного типа.
        Task task1 = new Task(
                "Домашка", "Выполнить ТЗ", Status.NEW);
        Task task2 = new Task(
                "Обед", "Приготовить макароны", Status.DONE);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(
                "Уборка дома", "Выполнить уборку дома");
        Epic epic2 = new Epic(
                "Дизайн", "Рассмотреть дизайн квартиры");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask(
                "Полы", "Помыть полы", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(
                "Посуда", "Помыть посуду", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask(
                "Ванная", "Посмотреть дизайны ванной 3.5кв.м.", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        //Вызвать разные методы интерфейса TaskManager и напечатать историю после каждого вызова.
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic2.getId());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

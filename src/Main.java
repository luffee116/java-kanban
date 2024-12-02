public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Пол", "Пропылесосить и помыть", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Окна", "Протереть внутри и снаружи", Status.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Обед", "Приготовить еду на обед");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Первое", "Приготовить борщ", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Второе", "Приготовить макароны по-флотски", Status.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Универ", "Дела по универу");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Java", "Написать код – трекер задач", Status.DONE, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("1––––––––––––");

        Subtask updatedSubtask1 = new Subtask("Первое", "Приготовить борщ", Status.DONE, epic1.getId());
        updatedSubtask1.setId(subtask1.getId());
        taskManager.updateSubtask(updatedSubtask1);
        Subtask updatedSubtask2 = new Subtask("Второе", "Приготовить макароны по-флотски", Status.DONE, epic1.getId());
        updatedSubtask2.setId(subtask2.getId());
        taskManager.updateSubtask(updatedSubtask2);

        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("2–––––––––––––");

        Task updatedTask1 = new Task("Пол", "Пропылесосить и помыть", Status.DONE);
        updatedTask1.setId(task1.getId());
        taskManager.updateTask(updatedTask1);
        System.out.println(taskManager.getAllTasks());

        System.out.println("3–––––––––––––");
        System.out.println(taskManager.getAllEpics());

        System.out.println("4–––––––––––––");
        System.out.println(taskManager.getEpicById(epic2.getId()));

        System.out.println("5–––––––––––––");
        taskManager.removeAllTasks();
        System.out.println(taskManager.getAllTasks());

        System.out.println("6–––––––––––––");
        taskManager.removeSubtaskById(subtask1.getId());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("7–––––––––––––");
        System.out.println(taskManager.getAllEpics());
        taskManager.removeEpicById(epic1.getId());
        System.out.println(taskManager.getAllEpics());


    }
}

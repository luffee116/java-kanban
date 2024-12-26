package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getHistory();

    ArrayList<Task> getAllTasks();

    Task getTaskById(int idTask);

    Subtask getSubtaskById(int subtaskId);

    Epic getEpicById(int epicId);

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic updatedEpic);

    void removeTaskById(int taskId);

    void removeSubtaskById(int subtaskId);

    void removeEpicById(int epicId);

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);
}

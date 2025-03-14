package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getAllTasks();

    Optional<Task> getTaskById(int idTask);

    Optional<Subtask> getSubtaskById(int subtaskId);

    Optional<Epic> getEpicById(int epicId);

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

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

    List<Subtask> getSubtasksOfEpic(int epicId);

    Set<Task> getTaskByPriority();

    int getLastId();
}

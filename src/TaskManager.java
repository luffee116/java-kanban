import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public int id = 1;

    public int generateId() {
        return id++;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear(); //тк удалили все эпики
    }

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Ошибка в ID Epic'a");
        }
    }

    public Task getTaskById(int idTask) {
        return tasks.get(idTask);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void updateTask(Task task) {
        if (tasks == null || !tasks.containsKey(task.getId())) {
            //System.out.println("Ошибка: задача с ID " + task.getId() + " не найдена.");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            //System.out.println("Ошибка: задача с ID " + subtask.getId() + " не найдена.");
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        updateEpicStatus(epic);
    }

    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic == null || !epics.containsKey(updatedEpic.getId())) {
            //System.out.println("Ошибка: задача с ID " + epic.getId() + " не найдена.");
            return;
        }
        Epic existingEpic = epics.get(updatedEpic.getId());
        updatedEpic.getSubtasksId().addAll(existingEpic.getSubtasksId());
        epics.put(updatedEpic.getId(), updatedEpic);
        updateEpicStatus(updatedEpic);

    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksId = epic.getSubtasksId();

        boolean allDone = true;
        boolean anyInProgress = false;

        for (int subtaskId : subtasksId) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }

            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    public void removeTaskById(int taskId){
        tasks.remove(taskId);
    }

    public void removeSubtaskById(int subtaskId){
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicID());
        subtasks.remove(subtaskId);
        epic.deleteSubtaskId(subtask.getId());
        updateEpic(epic);
    }

    public void removeEpicById (int epicId){
        epics.remove(epicId);
    }
}
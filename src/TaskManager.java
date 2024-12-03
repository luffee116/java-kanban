import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public int id = 1;

    private int generateId() {
        return id++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasksID();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void createTask(Task task) {
        if (task != null && task.getClass() == Task.class) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
        }
    }

    public void createEpic(Epic epic) {
        if (epic != null && epic.getClass() == Epic.class) {
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
        }
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null && subtask.getClass() == Subtask.class) {
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                subtask.setId(generateId());
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtaskId(subtask.getId());
                updateEpicStatus(epic);
            }
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
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask == null || !subtasks.containsKey(newSubtask.getId())) {
            return;
        }
        if (newSubtask.getEpicID() == subtasks.get(newSubtask.getId()).getEpicID()) {
            subtasks.put(newSubtask.getId(), newSubtask);
            Epic epic = epics.get(newSubtask.getEpicID());
            updateEpicStatus(epic);
        }
    }

    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic == null || !epics.containsKey(updatedEpic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(updatedEpic.getId());
        existingEpic.title = updatedEpic.title;
        existingEpic.description = updatedEpic.description;
        // ИСПРАВЛЕНО 03.12.2024
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksId = epic.getSubtasksId();

        boolean allDone = true;
        boolean anyInProgress = false;

        if (subtasksId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return; //ИСПРАВЛЕНО (03.12.2024)
        }

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

    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            subtasks.remove(subtaskId);
            epic.deleteSubtaskId(subtask.getId());
            updateEpicStatus(epic); // ИСПРАВЛЕНО 03.12.2024
        }
    }

    public void removeEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) { // ИСПРАВЛЕНО 03.12.2024
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOut = new ArrayList<>();
        if (epic != null) { // ИСПРАВЛЕНО 03.12.2024
            for (int subtaskId : epic.getSubtasksId()) {
                subtasksOut.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOut;
    }
}
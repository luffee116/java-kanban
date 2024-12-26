package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Manager.getDefaultHistory();
    public int id = 1;

    private int generateId() {
        return id++;
    }
    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasksID();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void createTask(Task task) {
        if (task != null && task.getClass() == Task.class) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null && epic.getClass() == Epic.class) {
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
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

    @Override
    public Task getTaskById(int idTask) {
        history.add(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicById(int epicId) {
        history.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        history.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks == null || !tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
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

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic == null || !epics.containsKey(updatedEpic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(updatedEpic.getId());
        existingEpic.setTitle(updatedEpic.getTitle());
        existingEpic.setDescription(updatedEpic.getDescription());
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksId = epic.getSubtasksId();

        boolean allDone = true;
        boolean anyInProgress = false;

        if (subtasksId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
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

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            subtasks.remove(subtaskId);
            epic.deleteSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOut = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                subtasksOut.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOut;
    }
}
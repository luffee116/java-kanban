package manager;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Manager.getDefaultHistory();
    public int id = 1;

    private int generateId() {
        return id++;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer entry : tasks.keySet()) {
            history.remove(entry);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer entry : subtasks.keySet()) {
            history.remove(entry);
        }
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasksID();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer entry : epics.keySet()) {
            history.remove(entry);
        }
        for (Integer entry : subtasks.keySet()) {
            history.remove(entry);
        }
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
        if (!tasks.containsKey(task.getId())) {
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
        history.remove(taskId);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            subtasks.remove(subtaskId);
            epic.deleteSubtaskId(subtask.getId());
            updateEpicStatus(epic);
            history.remove(subtaskId);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
                history.remove(subtaskId);
            }
            epics.remove(epicId);
            history.remove(epicId);
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasksOut = new ArrayList<>();
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                subtasksOut.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOut;
    }

    protected void addTaskFromFile(String[] lines, TaskType taskType, Status status) {
        switch (taskType) {
            case TASK ->
                    tasks.put(Integer.parseInt(lines[0]), new Task(Integer.parseInt(lines[0]), lines[2], lines[4], status));
            case SUBTASK -> {
                subtasks.put(Integer.parseInt(lines[0]), new Subtask(Integer.parseInt(lines[0]), lines[2], lines[4], status, Integer.parseInt(lines[lines.length - 1])));
                Epic tmpEpic = epics.get(Integer.parseInt(lines[lines.length - 1]));
                tmpEpic.addSubtaskId(Integer.parseInt(lines[0]));
                updateEpic(tmpEpic);
            }
            case EPIC ->
                    epics.put(Integer.parseInt(lines[0]), new Epic(Integer.parseInt(lines[0]), lines[2], lines[4], status));
        }
    }
}
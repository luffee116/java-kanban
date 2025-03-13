package manager;

import exceptions.InvalidTaskTimeException;
import model.*;
import util.DTF;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Manager.getDefaultHistory();
    private final Set<Task> tasksByPriority = new TreeSet<>(Comparator.comparing(Task::getTimeStart));
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
        return epics.values().stream().toList();
    }

    @Override
    public void removeAllTasks() {
        tasks.forEach((id, task) -> {
            history.remove(id);
            tasksByPriority.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.forEach((id, subtask) -> {
            history.remove(id);
            tasksByPriority.remove(subtask);
        });
        epics.forEach((id, epic) -> {
            epic.removeAllSubtasksID(); //UPDATED
            updateEpicStatus(epic); //UPDATED
            updateEpicTime(epic);
        });
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(history::remove); // UPDATED
        subtasks.forEach((id, subtask) -> {
            history.remove(id);
            tasksByPriority.remove(subtask);
        });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void createTask(Task task) {
        try {
            validateTask(task);
        } catch (InvalidTaskTimeException e) {
            throw new RuntimeException(e);
        }
        if (task != null && task.getClass() == Task.class) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            tasksByPriority.add(task);
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
        try {
            validateTask(subtask);
        } catch (InvalidTaskTimeException e) {
            throw new RuntimeException(e);
        }
        if (subtask != null && subtask.getClass() == Subtask.class) {
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                subtask.setId(generateId());
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtaskId(subtask.getId());
                tasksByPriority.add(subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
    }

    @Override
    public Optional<Task> getTaskById(int idTask) {
        history.add(tasks.get(idTask));
        Task task = tasks.get(idTask);

        return task != null ? Optional.of(task) : Optional.empty();
    }

    @Override
    public Optional<Epic> getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        history.add(epics.get(epicId));

        return epic != null ? Optional.of(epic) : Optional.empty();
    }

    @Override
    public Optional<Subtask> getSubtaskById(int subtaskId) {
        history.add(subtasks.get(subtaskId));
        Subtask subtask = subtasks.get(subtaskId);
        return subtask != null ? Optional.of(subtask) : Optional.empty();
    }

    @Override
    public void updateTask(Task newTask) {
        if (!tasks.containsKey(newTask.getId())) {
            return;
        }
        try {
            validateTask(newTask);
        } catch (InvalidTaskTimeException e) {
            throw new RuntimeException(e);
        }
        tasksByPriority.remove(tasks.get(newTask.getId())); // UPDATED
        tasks.put(newTask.getId(), newTask);
        tasksByPriority.add(newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (newSubtask == null || !subtasks.containsKey(newSubtask.getId())) {
            return;
        }

        try {
            validateTask(newSubtask);
        } catch (InvalidTaskTimeException e) {
            throw new RuntimeException(e);
        }

        if (newSubtask.getEpicID() == subtasks.get(newSubtask.getId()).getEpicID()) {
            tasksByPriority.remove(subtasks.get(newSubtask.getId()));
            subtasks.put(newSubtask.getId(), newSubtask);
            tasksByPriority.add(newSubtask);
            Epic epic = epics.get(newSubtask.getEpicID());
            updateEpicStatus(epic);
            updateEpicTime(epic);
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
        tasksByPriority.remove(tasks.get(taskId));
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
            updateEpicTime(epic);
            history.remove(subtaskId);
            tasksByPriority.remove(subtask);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                tasksByPriority.remove(subtasks.get(subtaskId)); // UPDATED
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
        return epic.getSubtasksId().stream().map(subtasks::get).toList();
    }

    @Override
    public Set<Task> getTaskByPriority() {
        return new LinkedHashSet<>(tasksByPriority);
    }

    protected void addTaskFromFile(String[] lines, TaskType taskType, Status status) {
        switch (taskType) {
            case TASK -> tasks.put(Integer.parseInt(lines[0]), new Task(Integer.parseInt(lines[0]),
                    lines[2],
                    lines[4],
                    status,
                    Duration.ofMinutes(Long.parseLong(lines[5])),
                    LocalDateTime.parse(lines[6], DTF.getDTF())));
            case SUBTASK -> {
                subtasks.put(Integer.parseInt(lines[0]), new Subtask(Integer.parseInt(lines[0]),
                        lines[2],
                        lines[4],
                        status,
                        Integer.parseInt(lines[5]),
                        Duration.parse(lines[6]),
                        LocalDateTime.parse(lines[7], DTF.getDTF())));
                Epic tmpEpic = epics.get(Integer.parseInt(lines[5]));
                tmpEpic.addSubtaskId(Integer.parseInt(lines[0]));
                updateEpicTime(tmpEpic);
                updateEpic(tmpEpic);
            }
            case EPIC -> epics.put(Integer.parseInt(lines[0]), new Epic(Integer.parseInt(lines[0]),
                    lines[2],
                    lines[4],
                    status,
                    lines[5].isBlank() ? null : Duration.parse(lines[5]),
                    lines[6].isBlank() ? null : LocalDateTime.parse(lines[6], DTF.getDTF())));

        }
    }

    protected void validateTask(Task task) throws InvalidTaskTimeException {
        List<Integer> collected = tasksByPriority.stream().filter(t -> t.getId() != task.getId())
                .filter(t ->
                        t.getTimeStart().isBefore(task.getTimeStart()) && t.getTimeEnd().isAfter(task.getTimeStart()) ||
                                t.getTimeStart().isBefore(task.getTimeEnd()) && t.getTimeEnd().isAfter(task.getTimeEnd()) ||
                                t.getTimeStart().isBefore(task.getTimeStart()) && t.getTimeEnd().isAfter(task.getTimeEnd()) ||
                                t.getTimeStart().isAfter(task.getTimeStart()) && t.getTimeEnd().isBefore(task.getTimeEnd()) ||
                                t.getTimeStart().equals(task.getTimeStart())).map(Task::getId).toList();
        if (!collected.isEmpty()) {
            throw new InvalidTaskTimeException("Задача с id = " + task.getId() + " пересекается с задачами с id = " + collected);
        }
    }

    protected void updateEpicTime(Epic epic) {
        LocalDateTime minimalTime = getMinimalTime(epic);
        long duration = calculateEpicDuration(epic);
        LocalDateTime timeEnd = getMaximalTime(epic);
        epic.setTimeStart(minimalTime);
        epic.setDuration(Duration.ofMinutes(duration));
        epic.setTimeEnd(timeEnd);
    }

    protected long calculateEpicDuration(Epic epic) {
        return getSubtasksOfEpic(epic.getId()).stream()
                .map(subtask -> Optional.ofNullable(subtask.getDuration())
                        .map(Duration::toMinutes)
                        .orElse(0L))
                .reduce(0L, Long::sum);
    }

    private LocalDateTime getMinimalTime(Epic epic) {
        return getSubtasksOfEpic(epic.getId()).stream().map(Task::getTimeStart).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
    }

    private LocalDateTime getMaximalTime(Epic epic) {
        return getSubtasksOfEpic(epic.getId()).stream().map(Task::getTimeEnd).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
    }
}

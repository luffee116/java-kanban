package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        final String title = "id,type,name,status,description,epic\n";
        List<String> list = new ArrayList<>();
        list.add(title);

        for (Task task : getAllTasks()) {
            list.add(task.formatToCVS());
        }
        for (Epic epic : getAllEpics()) {
            list.add(epic.formatToCVS());
        }
        for (Subtask subtask : getAllSubtasks()) {
            list.add(subtask.formatToCVS());
        }
        saveToCVS(list);
    }

    public void saveToCVS(List<String> list) throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Сохранение данных в файл недоступно");
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (String s : list) {
                br.write(s);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void load() {
        List<String> lines;
        try {
            lines = loadFromCVS();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Невозможно загрузить файл.");
        }
        lines.removeFirst();
        for (String line : lines) {
            formatFromCVS(line);
        }
    }

    public List<String> loadFromCVS() throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Невозможно загрузить данные из файла");
        }

        List<String> output = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            while (bf.ready()) {
                output.add(bf.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return output;
    }

    public void formatFromCVS(String line) {
        String[] lines = line.trim().split(",");
        TaskType taskType = TaskType.valueOf(lines[1]);
        switch (taskType) {
            case TASK -> super.createTask(new Task(Integer.parseInt(lines[0]),
                    lines[2],
                    lines[4],
                    returnStatusFromString(lines[3])));
            case SUBTASK -> super.createSubtask(new Subtask(Integer.parseInt(lines[0]),
                    lines[2],
                    lines[4],
                    returnStatusFromString(lines[3]),
                    Integer.parseInt(lines[lines.length - 1])));
            case EPIC -> super.createEpic(new Epic(Integer.parseInt(lines[0]),
                    lines[2],
                    lines[4],
                    returnStatusFromString(lines[3])));
        }
    }

    public Status returnStatusFromString(String line) {
        switch (line) {
            case "NEW" -> {
                return Status.NEW;
            }
            case "IN_PROGRESS" -> {
                return Status.IN_PROGRESS;
            }
            case "DONE" -> {
                return Status.DONE;
            }
            default -> throw new IllegalStateException("Неизвестное значение – " + line);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении всех задач – Task");
        }
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении всех задач – Subtask");
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении всех задач – Epic ");
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при создании задачи – " + task.getTitle());
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при создании задачи – " + epic.getTitle());
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при создании задачи – " + subtask.getTitle());
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка обновлении задачи");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка обновлении задачи");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка обновлении задачи");
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении задачи с id – " + taskId);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении задачи с id – " + subtaskId);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка при удалении задачи с id – " + epicId);
        }
    }
}

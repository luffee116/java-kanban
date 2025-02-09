package manager;

import java.io.File;

public class Manager {
    private Manager() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.load();
        return fileBackedTaskManager;
    }
}

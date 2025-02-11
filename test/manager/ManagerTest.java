package manager;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ManagerTest {

    @Test
    public void createTaskManager() {
        TaskManager manager = Manager.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class, manager);
    }

    @Test
    public void createHistoryManager() {
        HistoryManager manager = Manager.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, manager);
    }

    @Test
    public void createFileBackedTaskManager() {
        Path path;
        try {
            path = Files.createTempFile("test", ".cvs");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TaskManager actually = Manager.getFileBackedTaskManager(path.toFile());
        Assertions.assertInstanceOf(FileBackedTaskManager.class, actually);
    }

    @Test
    public void testLoadFromFile() {
        Path path;
        try {
            path = Files.createTempFile("test", ".cvs");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TaskManager actually = Manager.getFileBackedTaskManager(path.toFile());
        Assertions.assertInstanceOf(FileBackedTaskManager.class, actually);
    }

}

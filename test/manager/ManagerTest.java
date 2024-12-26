package manager;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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

}

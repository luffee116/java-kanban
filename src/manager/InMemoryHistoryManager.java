package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public ArrayList<Task> historyTask = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            historyTask.add(task);
            if (historyTask.size() > 10) {
                historyTask.removeFirst();
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory(){
        return new ArrayList<>(historyTask);
    }


}

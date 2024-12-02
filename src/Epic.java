import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String title,String description){
        super(title,description,Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void deleteSubtaskId(int subtaskId){
        int index = 0;
        for (int id : subtasksId){
            if (id == subtaskId){
                subtasksId.remove(index);
            } else {
                index++;
            }
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", Название = '" + title + '\'' +
                ", Описание = '" + description + '\'' +
                " Статус = " + status +
                ", SubtasksId = " + subtasksId +
                '}';
    }
}
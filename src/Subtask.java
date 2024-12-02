public class Subtask extends Task {

    private final int epicID;

    public Subtask(String title, String description, Status status, int epicID){
        super(title,description,status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask {" +
                ", Название = '" + title + '\'' +
                ", Описание = '" + description + '\'' +
                ", Epic Id = " + epicID +
                ", Статус = " + status +
                '}';
    }
}

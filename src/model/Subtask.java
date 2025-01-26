package model;

public class Subtask extends Task {

    private int epicID;

    public Subtask(String title, String description, Status status, int epicID) {
        super(title, description, status);
        this.epicID = epicID;
    }

    public Subtask(int id, String title, String description, Status status, int epicID) {
        super(id, title, description, status);
        this.epicID = epicID;
    }

    @Override
    public String formatToCVS() {
        return String.format("%s,%s,%s,%s,%s,%s\n", getId(), TaskType.SUBTASK, getTitle(), getStatus(), getDescription(), getEpicID());
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "model.Subtask {" +
                ", Название = '" + title + '\'' +
                ", Описание = '" + description + '\'' +
                ", model.Epic Id = " + epicID +
                ", Статус = " + status +
                '}';
    }
}

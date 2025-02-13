package model;

import java.util.ArrayList;

public class Epic extends Task {
    protected final ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasksId = new ArrayList<>();
    }

    @Override
    public String formatToCVS() {
        return String.format("%s,%s,%s,%s,%s\n", getId(), TaskType.EPIC, getTitle(), getStatus(), getDescription());
    }

    public ArrayList<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void deleteSubtaskId(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    }

    public void removeAllSubtasksID() {
        subtasksId.clear();
    }


    @Override
    public String toString() {
        return "model.Epic{" +
                " Название = '" + title + '\'' +
                ", Описание = '" + description + '\'' +
                " Статус = " + status + '}';
    }
}

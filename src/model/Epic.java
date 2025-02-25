package model;

import util.DTF;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    protected final ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String title, String description, Duration duration, LocalDateTime timeStart) {
        super(title, description, Status.NEW, duration, timeStart);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime timeStart) {
        super(id, title, description, status, duration, timeStart);
        this.subtasksId = new ArrayList<>();
    }

    @Override
    public String formatToCVS() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                getId(),
                TaskType.EPIC,
                getTitle(),
                getStatus(),
                getDescription(),
                getDuration(),
                getTimeStart().format(DTF.getDTF()),
                getTimeEnd().format(DTF.getDTF()));
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
        return "Epic{" +
                " id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", timeStart=" + timeStart.format(DTF.getDTF()) +
                ", timeEnd=" + timeEnd.format(DTF.getDTF()) +
                '}';
    }
}

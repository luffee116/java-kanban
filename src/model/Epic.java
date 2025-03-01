package model;

import util.DTF;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected final ArrayList<Integer> subtasksId;
    protected LocalDateTime timeEnd;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, null, null);
        this.subtasksId = new ArrayList<>();
        this.timeEnd = null;
    }

    // Для Юнит тестов
    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW, null, null);
        this.subtasksId = new ArrayList<>();
        this.timeEnd = null;
    }

    public Epic(String title, String description, Duration duration, LocalDateTime timeStart) {
        super(title, description, Status.NEW, duration, timeStart);
        this.subtasksId = new ArrayList<>();
        this.timeEnd = getTimeEnd();
    }

    //Для Юнит Тестов
    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime timeStart) {
        super(id, title, description, status, duration, timeStart);
        this.subtasksId = new ArrayList<>();
        this.timeEnd = getTimeEnd();
    }

    @Override
    public String formatToCVS() {
        String startTime = Objects.isNull(this.timeStart) ? " " : this.timeStart.format(DTF.getDTF());
        String duration = Objects.isNull(this.duration) ? " " : this.duration.toString();
        String endTime = Objects.isNull(this.timeEnd) ? " " : this.timeEnd.format(DTF.getDTF());
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                getId(),
                TaskType.EPIC,
                getTitle(),
                getStatus(),
                getDescription(),
                duration,
                startTime,
                endTime);
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

    public void setTimeEnd(LocalDateTime time) {
        timeEnd = time;
    }

    @Override
    public String toString() {
        String startTime = Objects.isNull(this.timeStart) ? " " : this.timeStart.format(DTF.getDTF());
        String duration = Objects.isNull(this.duration) ? " " : String.valueOf(this.duration.toMinutes());
        String endTime = Objects.isNull(this.timeEnd) ? " " : this.timeEnd.format(DTF.getDTF());
        return "Epic{" +
                " id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", timeStart=" + startTime +
                ", timeEnd=" + endTime +
                '}';
    }
}

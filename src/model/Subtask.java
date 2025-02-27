package model;

import util.DTF;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicID;

    public Subtask(String title, String description, Status status, int epicID, Duration duration, LocalDateTime timeStart) {
        super(title, description, status, duration, timeStart);
        this.epicID = epicID;
    }

    public Subtask(int id, String title, String description, Status status, int epicID, Duration duration, LocalDateTime timeStart) {
        super(id, title, description, status, duration, timeStart);
        this.epicID = epicID;
    }

    @Override
    public String formatToCVS() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                getId(),
                TaskType.SUBTASK,
                getTitle(),
                getStatus(),
                getDescription(),
                getEpicID(),
                getDuration(),
                getTimeStart().format(DTF.getDTF()),
                getTimeEnd().format(DTF.getDTF()));
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", timeStart=" + timeStart.format(DTF.getDTF()) +
                ", timeEnd=" + getTimeEnd().format(DTF.getDTF()) +
                '}';
    }
}

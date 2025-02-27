package model;

import util.DTF;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    int id;
    String title;
    String description;
    Status status;
    Duration duration;
    LocalDateTime timeStart;

    public String formatToCVS() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                getId(),
                TaskType.TASK,
                getTitle(),
                getStatus(),
                getDescription(),
                duration.toMinutes(),
                timeStart.format(DTF.getDTF()),
                getTimeEnd().format(DTF.getDTF()));
    }

    public Task(String title,
                String description,
                Status status,
                Duration duration,
                LocalDateTime timeStart) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.timeStart = timeStart;
    }

    public Task(int id,
                String title,
                String description,
                Status status,
                Duration duration,
                LocalDateTime timeStart) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.timeStart = timeStart;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", timeStart=" + timeStart.format(DTF.getDTF()) +
                ", timeEnd=" + getTimeEnd().format(DTF.getDTF()) +
                '}';
    }

    public LocalDateTime getTimeEnd() {
        if (timeStart != null && duration != null) {
            return timeStart.plus(duration);
        }
        return null;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }
}

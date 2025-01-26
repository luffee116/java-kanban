package model;

public enum TaskType {
    TASK("TASK"),
    SUBTASK("SUBTASK"),
    EPIC("EPIC");

    final String taskType;

    TaskType(String taskType) {
        this.taskType = taskType;
    }
}

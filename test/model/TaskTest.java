package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

class TaskTest {
    Task task1;
    Task task2;

    @BeforeEach
    void init() {
        task1 = new Task(1, "Помыть пол", "Начисто", Status.NEW);
        task2 = new Task(1, "noTest", "noTest", Status.NEW);
    }

    @Test
    void testEquals() {
        boolean equals = task1.equals(task2);
        Assertions.assertTrue(equals);
    }

    @Test
    public void testGetId() {
        int expected = 1;
        int actually = task1.getId();
        Assertions.assertEquals(expected, actually, "Не получен ID");
    }

    @Test
    public void testSetId() {
        int expected = 2;
        task1.setId(2);
        int actually = task1.getId();
        Assertions.assertEquals(expected, actually, "ID не устанавливается");
    }

    @Test
    public void testGetTitle() {
        String expected = "Помыть пол";
        String actual = task1.getTitle();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetDescription() {
        String expected = "Начисто";
        String actual = task1.getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetStatus() {
        Status expected = Status.NEW;
        Status actual = task1.getStatus();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testSetTitle() {
        String expected = "Помыть потолок";
        task1.setTitle("Помыть потолок");
        String actual = task1.getTitle();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testSetDescription() {
        String expected = "Зачем его мыть?";
        task1.setDescription("Зачем его мыть?");
        String actual = task1.getDescription();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testSetStatus() {
        Status expected = Status.DONE;
        task1.setStatus(Status.DONE);
        Status actual = task1.getStatus();
        Assertions.assertEquals(expected, actual);
    }
}

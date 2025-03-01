package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void init() {
        subtask1 = new Subtask(1, "Test", "Test", Status.NEW, 1, Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 1, 1, 1));
        subtask2 = new Subtask(1, "Another", "Another", Status.DONE, 2, Duration.ofMinutes(1), LocalDateTime.of(2025, 1, 2, 2, 2));
    }

    @Test
    void testEqualsSubtask() {
        Assertions.assertEquals(subtask1, subtask2);
    }

    @Test
    void testGetEpicID() {
        int expected = 2;
        int actual = subtask2.getEpicID();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSetEpicID() {
        int expected = 3;
        subtask1.setEpicID(3);
        int actual = subtask1.getEpicID();
        Assertions.assertEquals(expected, actual);
    }


}

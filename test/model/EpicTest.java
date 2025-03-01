package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class EpicTest {
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void init() {
        epic1 = new Epic(1, "Гигиена", "Гигиена", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2002, 1, 25, 10, 0));
        epic2 = new Epic(2, "Досуг", "Досуг");
        subtask1 = new Subtask(3, "Почистить зубы", "Тщательно", Status.DONE, epic1.getId(), Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 20, 13, 0));
        subtask2 = new Subtask(4, "Посмотреть кино", "Ужасы", Status.NEW, epic2.getId(), Duration.ofMinutes(10), LocalDateTime.of(2025, 10, 1, 10, 0));
    }

    @Test
    void testAddSubtaskID() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(3);
        expected.add(4);
        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());
        ArrayList<Integer> actually = epic1.getSubtasksId();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveSubtaskID() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(4);
        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());
        epic1.deleteSubtaskId(subtask1.getId());
        ArrayList<Integer> actually = epic1.getSubtasksId();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllSubtasksID() {
        epic1.addSubtaskId(subtask1.getId());
        epic1.addSubtaskId(subtask2.getId());
        epic1.removeAllSubtasksID();
        ArrayList<Integer> actually = epic1.getSubtasksId();
        Assertions.assertEquals(new ArrayList<Integer>(), actually);
    }

    @Test
    void testGetSubtasksID() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(3);
        epic1.addSubtaskId(subtask1.getId());
        ArrayList<Integer> actually = epic1.getSubtasksId();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void shouldBeEqualsIfHaveSameID() {
        epic1.setId(1);
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2, "Эпики не равны");
    }

}

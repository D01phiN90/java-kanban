package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void shouldReturnTrueIfTasksHaveSameId() {
        Task task1 = new Task("Task 1", Status.NEW, "Description 1");
        task1.setId(1);

        Task task2 = new Task("Task 2", Status.DONE, "Description 2");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны");
    }
}

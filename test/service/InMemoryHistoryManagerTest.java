package service;

import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void testAddAndRemove() {
        // Проверяем, что история изначально пуста
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой при инициализации");

        Task task1 = createTask(1, "Task 1", Status.NEW, "Description 1");
        Task task2 = createTask(2, "Task 2", Status.NEW, "Description 2");

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1); // повторное добавление

        // Проверяем, что история содержит две уникальные задачи в правильном порядке
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две уникальные задачи");
        assertEquals(task2, history.get(0), "Последовательность истории нарушена");
        assertEquals(task1, history.get(1), "Последовательность истории нарушена");

        // Тестируем удаление
        historyManager.remove(2);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task1, history.get(0), "Оставшийся элемент должен быть task1");
    }

    private Task createTask(int id, String name, Status status, String description) {
        Task task = new Task(name, status, description);
        task.setId(id);
        return task;
    }
}

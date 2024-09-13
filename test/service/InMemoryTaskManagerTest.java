package service;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @Test
    public void shouldNotAllowSubtaskToBeItsOwnEpic() {
        SubTask subTask = new SubTask("SubTask", Status.NEW, "Description", 1);
        subTask.setId(1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubTask(subTask);
        });
    }

    @Test
    public void shouldReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "Менеджер задач должен быть проинициализирован");
        assertNotNull(historyManager, "Менеджер истории должен быть проинициализирован");
    }

    @Test
    public void shouldAddAndFindTasksById() {
        Task task = new Task("Task", Status.NEW, "Description");
        taskManager.createTask(task);

        Epic epic = new Epic("Epic", "Epic Description");
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask", Status.NEW, "Subtask Description", epic.getId());
        taskManager.createSubTask(subTask);

        assertEquals(task, taskManager.getTask(task.getId()), "Задача должна быть найдена");
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпик должен быть найден");
        assertEquals(subTask, taskManager.getSubTask(subTask.getId()), "Подзадача должна быть найдена");
    }

    @Test
    public void shouldNotConflictWithGeneratedAndAssignedIds() {
        Task task = new Task("Task 1", Status.NEW, "Description 1");
        taskManager.createTask(task);

        Task taskWithSetId = new Task("Task 2", Status.NEW, "Description 2");
        taskWithSetId.setId(10);
        taskManager.createTask(taskWithSetId);  // Добавляем задачу с установленным ID

        assertNotEquals(task.getId(), taskWithSetId.getId(), "Идентификаторы не должны совпадать");
    }
}

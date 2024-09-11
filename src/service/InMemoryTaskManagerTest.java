package service;

import model.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @Test
    public void shouldReturnTrueIfTasksHaveSameId() {
        Task task1 = new Task("Task 1", Status.NEW, "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", Status.DONE, "Description 2");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны");
    }

    @Test
    public void shouldReturnTrueIfEpicAndSubtaskHaveSameId() {
        Epic epic = new Epic("Epic", "Epic Description");
        epic.setId(1);

        SubTask subTask = new SubTask("SubTask", Status.NEW, "Subtask Description", 1);
        subTask.setId(1);

        assertEquals(epic, subTask, "Наследники Task с одинаковыми ID должны быть равны");
    }

    @Test
    public void shouldNotAllowAddingEpicToItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Epic Description");
        epic.setId(1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubTask(1);  // Пробуем добавить себя как подзадачу
        });

        assertEquals("Epic cannot add itself as a subtask.", thrown.getMessage());
    }

    @Test
    public void shouldNotAllowSubtaskToBeItsOwnEpic() {
        SubTask subTask = new SubTask("SubTask", Status.NEW, "Description", 1);
        subTask.setId(1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubTask(subTask);
        }, "Subtask cannot reference itself as an epic.");
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
        taskWithSetId.setId(10);  // Явно задаём ID
        taskManager.createTask(taskWithSetId);

        assertEquals(1, task.getId(), "Генерируемый ID не должен конфликтовать");
        assertEquals(10, taskWithSetId.getId(), "Заданный вручную ID должен оставаться");
    }

    @Test
    public void shouldNotChangeTaskFieldsWhenAddedToManager() {
        Task task = new Task("Task", Status.NEW, "Description");
        taskManager.createTask(task);

        Task savedTask = taskManager.getTask(task.getId());
        assertEquals(task.getName(), savedTask.getName(), "Название задачи должно оставаться неизменным");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задачи должно оставаться неизменным");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статус задачи должен оставаться неизменным");
    }

    @Test
    public void shouldSaveTaskInHistoryWithPreviousVersion() {
        Task task = new Task("Task", Status.NEW, "Description");
        taskManager.createTask(task);

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать задачу");
        assertEquals(Status.NEW, history.get(0).getStatus(), "История должна хранить прежнюю версию задачи");
    }
}

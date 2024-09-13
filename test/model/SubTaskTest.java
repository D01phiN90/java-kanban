package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

public class SubTaskTest {

    @Test
    public void shouldNotAllowSubtaskToBeItsOwnEpic() {
        // Создаем подзадачу
        SubTask subTask = new SubTask("SubTask", Status.NEW, "Description", 1); // epicId = 1

        // Создаем менеджер задач
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        // Проверяем, что будет выброшено исключение, если подзадача ссылается на свой же эпик
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubTask(subTask); // Вызов метода, который должен выбросить исключение
        });
    }
}

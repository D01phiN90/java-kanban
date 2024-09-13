package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void shouldNotAllowAddingEpicToItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Epic Description");
        epic.setId(1);

        assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubTask(1);  // Пробуем добавить себя как подзадачу
        });
    }
}

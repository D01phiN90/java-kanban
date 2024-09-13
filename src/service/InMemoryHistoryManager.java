package service;

import model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        // Удаляем старую версию задачи по ID
        history.removeIf(t -> t.getId() == task.getId());

        // Ограничение размера истории до 10 задач
        if (history.size() == 10) {
            history.remove(0);
        }

        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}

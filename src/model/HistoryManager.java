package model;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
    void remove(int id);  // добавляем метод для удаления
    void clear();         // добавляем метод для очистки истории
}

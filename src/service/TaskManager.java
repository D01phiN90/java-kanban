package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTasksByEpic(int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    List<Task> getHistory();  //метод для получения истории
}

package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int seq = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int generateId() {
        return ++seq;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Epic с ID " + subTask.getEpicId() + " не существует.");
        }
        if (subTask.getEpicId() == subTask.getId()) {
            throw new IllegalArgumentException("Подзадача не может ссылаться на саму себя как на эпик.");
        }

        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTask(subTask.getId());
        updateEpicStatus(epic);
        return subTask;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Epic с ID " + epicId + " не существует.");
        }
        return epic.getSubTasksIds().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(Task task) {
        Task existingTask = tasks.get(task.getId());
        if (existingTask != null) {
            tasks.put(task.getId(), task);
            historyManager.add(existingTask);
        } else {
            throw new IllegalArgumentException("Task с ID " + task.getId() + " не существует.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        } else {
            throw new IllegalArgumentException("Epic с ID " + epic.getId() + " не существует.");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask existingSubTask = subTasks.get(subTask.getId());
        if (existingSubTask != null) {
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        } else {
            throw new IllegalArgumentException("SubTask с ID " + subTask.getId() + " не существует.");
        }
    }

    @Override
    public void removeTask(int id) {
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic != null) {
            for (Integer subTaskId : removedEpic.getSubTasksIds()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        SubTask removedSubTask = subTasks.remove(id);
        if (removedSubTask != null) {
            Epic epic = epics.get(removedSubTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(id);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasksIds().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean hasNew = false;
        boolean hasInProgress = false;

        for (int subTaskId : epic.getSubTasksIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask != null) {
                switch (subTask.getStatus()) {
                    case NEW -> hasNew = true;
                    case IN_PROGRESS -> hasInProgress = true;
                    case DONE -> {}
                }
            }
        }

        if (hasNew && !hasInProgress) {
            epic.setStatus(Status.NEW);
        } else if (!hasNew && !hasInProgress) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}

package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskService {
    // Хранение
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int seq = 0; // Счетчик


    private int generateId() {
        return ++seq; // Увеличиваем на 1
    }

    // Метод для создания нового task.
    public Task createTask(Task task) {
        task.setId(generateId()); // Присвоение Id.
        tasks.put(task.getId(), task); // Добавление task в коллекцию tasks.
        return task; // Возврат созданного task.
    }

    // Метод для создания нового epic.
    public Epic createEpic(Epic epic) {
        epic.setId(generateId()); // Присвоение Id.
        epics.put(epic.getId(), epic); // Добавление epic в коллекцию epics.
        return epic; // Возврат созданного epic.
    }

    // Метод для создания новой subTask.
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId()); // Поиск эпика, к которому относится подзадача.

        // Проверка наличия epic перед добавлением subTask.
        if (epic != null) {
            subTask.setId(generateId()); // Присвоение Id subTask после проверки, что есть epic.
            epic.addSubTask(subTask.getId()); // Добавление Id subTask в epic.
            subTasks.put(subTask.getId(), subTask); // Добавление subTask в коллекцию subTasks.
            updateEpicStatus(epic); // Обновление статуса epic на основе статусов subTask.
        } else {
            System.out.println("Эпик с таким id " + subTask.getEpicId() + " не найден. Подзадачу нельзя добавить.");
            return null;
        }

        return subTask; // Возврат созданного subTask.
    }

    // Метод для получения task по id.
    public Task getTask(int id) {
        return tasks.get(id); // Возврат task по id.
    }

    // Метод для получения epic по id.
    public Epic getEpic(int id) {
        return epics.get(id); // Возврат epic по id.
    }

    // Метод для получения subTask по id.
    public SubTask getSubTask(int id) {
        return subTasks.get(id); // Возврат подзадачи по её идентификатору.
    }

    // Метод для получения списка всех task.
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values()); // Возврат списка всех task.
    }

    // Метод для получения списка всех epic.
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values()); // Возврат списка всех epic.
    }

    // Метод для получения списка всех subTask.
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values()); // Возврат списка всех subTask.
    }

    // Метод для получения списка всех subTask определенного epic.
    public List<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId); // Поиск эпика по id.
        if (epic == null) {
            return null; // Возврат null, если эпик не найден.
        }
        List<SubTask> subTasksList = new ArrayList<>(); // Создание списка subTask.
        for (Integer subTaskId : epic.getSubTasksIds()) { // Проход по всем id подзадач epic.
            subTasksList.add(subTasks.get(subTaskId)); // Добавление subTaskId в список.
        }
        return subTasksList; // Возврат списка subTask.
    }

    // Метод для обновления task.
    public void updateTask(Task task) {
        // Проверка наличия task.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task); // Обновление task.
        } else {
            System.out.println("Task with id " + task.getId() + " not found.");
        }
    }

    // Метод для обновления epic.
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId()); // Проверка наличия epic.
        if (existingEpic != null) {
            existingEpic.setName(epic.getName()); // Обновление имени epic.
            existingEpic.setDescription(epic.getDescription()); // Обновление Description epic.
            updateEpicStatus(existingEpic); // Выясняем статус эпика на основе подзадач.
        } else {
            System.out.println("Epic with id " + epic.getId() + " not found.");
        }
    }

    // Метод для обновления subTask.
    public void updateSubTask(SubTask subTask) {
        // Проверка наличия SubTask.
        if (subTasks.containsKey(subTask.getId())) {
            Epic epic = epics.get(subTask.getEpicId()); // Получение epic.

            if (epic != null) {
                subTasks.put(subTask.getId(), subTask); // Если такой epic есть, обновляем subTask
                updateEpicStatus(epic); // Обновление Status epic.
            } else {
                System.out.println("Эпик с id " + subTask.getEpicId() + " не найден.");
            }
        } else {
            System.out.println("Подзада с id " + subTask.getId() + " не найдена");
        }
    }

    // Метод для обновления Status epic.
    private void updateEpicStatus(Epic epic) {
        List<Integer> subTaskIds = epic.getSubTasksIds(); // Получение списка id subTask эпика.
        if (subTaskIds.isEmpty()) { // Если у эпика нет подзадач.
            epic.setStatus(Status.NEW); // Устанавливаем статус NEW.
            return;
        }

        boolean allDone = true; // Флаг, который будет ложным, если найдется хотя бы одна незавершенная подзадача.
        boolean anyInProgress = false; // Флаг для проверки наличия подзадач в статусе IN_PROGRESS.

        for (int subTaskId : subTaskIds) { // Проход по всем подзадачам эпика.
            SubTask subTask = subTasks.get(subTaskId); // Получение подзадачи по идентификатору.
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true; // Если есть хотя бы одна подзадача в процессе.
                allDone = false; // Все подзадачи не могут быть завершены.
            } else if (subTask.getStatus() != Status.DONE) {
                allDone = false; // Если есть хотя бы одна подзадача не в статусе DONE.
            }
        }

        if (allDone) { // Если все подзадачи завершены.
            epic.setStatus(Status.DONE); // Устанавливаем статус DONE для эпика.
        } else if (anyInProgress) { // Если есть подзадачи в процессе выполнения.
            epic.setStatus(Status.IN_PROGRESS); // Устанавливаем статус IN_PROGRESS для эпика.
        } else {
            epic.setStatus(Status.NEW); // В противном случае устанавливаем статус NEW.
        }
    }

    // Метод для удаления task по её id.
    public void removeTask(int id) {
        tasks.remove(id); // Удаление задачи.
    }

    // Метод для удаления epic по его id.
    public void removeEpic(int id) {
        Epic epic = epics.remove(id); // Удаление эпика и возврат, для проверки подзадач.
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTasksIds()) { // Проход по всем подзадачам удаляемого эпика.
                subTasks.remove(subTaskId); // Удаление подзадач.
            }
        }
    }

    // Метод для удаления subTask по id.
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.remove(id); // Удаление подзадачи и возврат удаленной подзадачи.
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId()); // Получение эпика, к которому относилась подзадача.
            if (epic != null) {
                epic.removeSubTask(id); // Удаление подзадачи из эпика.
                updateEpicStatus(epic); // Обновление статуса эпика после удаления подзадачи.
            }
        }
    }

    // Метод для удаления всех task.
    public void removeAllTasks() {
        tasks.clear(); // Очистка tasks.
    }

    // Метод для удаления всех epic.
    public void removeAllEpics() {
        epics.clear(); // Очистка epics.
        subTasks.clear(); // Очистка subTasks, т.к. все подзадачи принадлежат только epics.
    }

    // Метод для удаления всех subTask.
    public void removeAllSubTasks() {
        subTasks.clear(); // Очистка коллекции subTasks.
        for (Epic epic : epics.values()) { // Проход по всем epics.
            epic.clearSubTasks(); // Очистка списка subTask каждого эпика.
            updateEpicStatus(epic); // Обновление статуса каждого эпика.
        }
    }
}
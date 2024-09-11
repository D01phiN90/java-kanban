import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskService service = new TaskService();

        // Создаем задачи
        Task task1 = service.createTask(new Task("Task 1", Status.NEW, "Description 1"));
        Task task2 = service.createTask(new Task("Task 2", Status.NEW, "Description 2"));

        System.out.println("Имя задачи 1 до обновления: " + task1.getName());
        System.out.println("Описание задачи 1 до обновления: " + task1.getDescription());
        System.out.println("Имя задачи 2: " + task2.getName());
        System.out.println("Описание задачи 2: " + task2.getDescription());

        // Изменение имени и описания task1
        task1.setName("Updated Task 1");
        task1.setDescription("Updated Description 1");
        service.updateTask(task1);

        // Проверяем task1
        System.out.println("Имя задачи 1 после обновления: " + task1.getName());
        System.out.println("Описание задачи 1 после обновления: " + task1.getDescription());

        // Создаем эпики
        Epic epic1 = service.createEpic(new Epic("Epic 1", "Description Epic 1"));
        Epic epic2 = service.createEpic(new Epic("Epic 2", "Description Epic 2"));

        // Создаем подзадачи для epic1
        SubTask subTask1 = service.createSubTask(new SubTask("Subtask 1", Status.NEW, "Subtask 1 Description", epic1.getId()));
        SubTask subTask2 = service.createSubTask(new SubTask("Subtask 2", Status.NEW, "Subtask 2 Description", epic1.getId()));

        // Создаем подзадачу для epic2
        SubTask subTask3 = service.createSubTask(new SubTask("Subtask 3", Status.NEW, "Subtask 3 Description", epic2.getId()));

        System.out.println("Все задачи: " + service.getAllTasks());
        System.out.println("Все эпики: " + service.getAllEpics());
        System.out.println("Все подзадачи: " + service.getAllSubTasks());

        // Получение и вывод подзадач по epic1
        List<SubTask> epic1SubTasks = service.getSubTasksByEpic(epic1.getId());
        System.out.println("Подзадачи эпика 1: " + epic1SubTasks);

        // Обновляем статус task1 и subTask1, subTask2, subTask3
        task1.setStatus(Status.IN_PROGRESS);
        service.updateTask(task1);

        subTask1.setStatus(Status.IN_PROGRESS);
        service.updateSubTask(subTask1);

        subTask2.setStatus(Status.DONE);
        service.updateSubTask(subTask2);

        subTask3.setStatus(Status.DONE);
        service.updateSubTask(subTask3);

        // Печатаем обновленные task1 и epic1, epic2
        System.out.println("Обновленная задача 1: " + service.getTask(task1.getId()));
        System.out.println("Обновленный эпик 1: " + service.getEpic(epic1.getId()));
        System.out.println("Обновленный эпик 2: " + service.getEpic(epic2.getId()));

        // Получение подзадачи по ID
        SubTask retrievedSubTask = service.getSubTask(subTask1.getId());
        System.out.println("Полученная подзадача 1: " + retrievedSubTask);

        // Обновляем epic2 и проверяем
        epic2.setDescription("Обновленное описание эпика 2");
        service.updateEpic(epic2);
        System.out.println("Обновленный эпик 2 после изменения описания: " + service.getEpic(epic2.getId()));

        // Удаление task1 и epic1
        service.removeTask(task1.getId());
        service.removeEpic(epic1.getId());

        // Оставшиеся задачи и эпики
        System.out.println("Оставшиеся задачи: " + service.getAllTasks());
        System.out.println("Оставшиеся эпики: " + service.getAllEpics());

        // Удаляем subTask3
        service.removeSubTask(subTask3.getId());
        System.out.println("Оставшиеся подзадачи: " + service.getAllSubTasks());

        // Удаление всех эпиков и подзадач
        service.removeAllEpics();
        service.removeAllSubTasks();

        // Печатаем оставшиеся задачи, эпики и подзадачи
        System.out.println("Все задачи после удаления эпиков и подзадач: " + service.getAllTasks());
        System.out.println("Все эпики после удаления: " + service.getAllEpics());
        System.out.println("Все подзадачи после удаления: " + service.getAllSubTasks());

        // Удаление всех задач, эпиков и подзадач
        service.removeAllTasks();
        System.out.println("Все задачи после удаления: " + service.getAllTasks());
        System.out.println("Все эпики после удаления: " + service.getAllEpics());
        System.out.println("Все подзадачи после удаления: " + service.getAllSubTasks());
    }
}

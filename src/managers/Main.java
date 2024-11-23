package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача_1", "Первая задача",
                LocalDateTime.of(2024, 10, 9, 15, 30), Duration.ofMinutes(60));
        task1 = manager.addTask(task1);
        Task task2 = new Task("Задача_2", "Вторая задача",
                LocalDateTime.of(2024, 10, 10, 12, 30), Duration.ofMinutes(30));
        task2 = manager.addTask(task2);

        Epic epic1 = new Epic("Эпик_1", "Первый эпик");
        epic1 = manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача_1", "Первая подзадача",
                LocalDateTime.of(2024, 11, 10, 13, 30), Duration.ofMinutes(45), epic1);
        subtask1 = manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача_2", "Вторая подзадача",
                LocalDateTime.of(2024, 12, 10, 13, 45), Duration.ofMinutes(30), epic1);
        subtask2 = manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик_2", "Второй эпик");
        epic2 = manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача", "Подзадача", epic2);
        subtask3 = manager.addSubtask(subtask3);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        System.out.println(task1);
        System.out.println(task2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);
        System.out.println(epic1);
        System.out.println(epic2);

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        manager.getEpicById(2);
        manager.getEpicById(5);
        manager.getTaskById(1);
        manager.getSubtaskById(6);
        manager.getEpicById(5);
        manager.getEpicById(2);
        manager.getEpicById(2);
        manager.getSubtaskById(6);
        manager.getTaskById(1);
        manager.getSubtaskById(6);

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            for (Task task : manager.getSubtasksByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistoryList()) {
            System.out.println(task);
        }
    }
}

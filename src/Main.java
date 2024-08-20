import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Задача_1", "Первая задача");
        task1 = manager.addTask(task1);
        Task task2 = new Task("Задача_2", "Вторая задача");
        task2 = manager.addTask(task2);

        Epic epic1 = new Epic("Эпик_1", "Первый эпик");
        epic1 = manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача_1", "Первая подзадача");
        subtask1 = manager.addSubtask(epic1, subtask1);
        Subtask subtask2 = new Subtask("Подзадача_2", "Вторая подзадача");
        subtask2 = manager.addSubtask(epic1, subtask2);

        Epic epic2 = new Epic("Эпик_2", "Второй эпик");
        epic2 = manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача", "Подзадача");
        subtask3 = manager.addSubtask(epic2, subtask3);

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

        manager.getEpicById(5);
        manager.getTaskById(1);
        manager.getSubtaskById(6);
        manager.getEpicById(2);

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
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}

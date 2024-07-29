import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();
        Task task1 = manager.createAndSaveTask("Задача_1", "Первая задача");
        Task task2 = manager.createAndSaveTask("Задача_2", "Вторая задача");
        Epic epic1 = manager.createAndSaveEpic("Эпик_1", "Первый эпик");
        Subtask subtask1 = manager.createAndSaveSubtask(epic1, "Подзадача_1", "Первая подзадача");
        Subtask subtask2 = manager.createAndSaveSubtask(epic1, "Подзадача_2", "Вторая подзадача");
        Epic epic2 = manager.createAndSaveEpic("Эпик_2", "Второй эпик");
        Subtask subtask3 = manager.createAndSaveSubtask(epic2, "Подзадача", "Подзадача");
        System.out.println(manager.getTasksMap());
        System.out.println(manager.getEpicsMap());
        System.out.println(manager.getSubtasksMap());
        manager.updateTask(task1, Status.IN_PROGRESS);
        manager.updateTask(task2, Status.DONE);
        manager.updateSubtask(subtask1, Status.IN_PROGRESS);
        manager.updateSubtask(subtask2, Status.DONE);
        manager.updateSubtask(subtask3, Status.DONE);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);
        System.out.println(epic1);
        System.out.println(epic2);
        manager.deleteTaskById(0);
        manager.deleteEpicById(2);
        System.out.println(manager.getTasksMap());
        System.out.println(manager.getEpicsMap());
        System.out.println(manager.getSubtasksMap());

    }
}

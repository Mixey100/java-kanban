import exceptions.ManagerValidateException;
import managers.InMemoryTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManager newManager() throws IOException {
        return new InMemoryTaskManager();
    }

    @Test
    void testShouldNotSaveTaskInPrioritizedTasksIfIntersect() {
        Task task = new Task("Task", "Task_description",
                LocalDateTime.of(2024, 10, 9, 15, 30), Duration.ofMinutes(30));
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Stubtask", "Subtask_description",
                LocalDateTime.of(2024, 10, 10, 15, 15), Duration.ofMinutes(45));
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_description_2",
                LocalDateTime.of(2024, 10, 10, 15, 0), Duration.ofMinutes(75));

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(epic, subtask);

        Assertions.assertThrows(ManagerValidateException.class,
                () -> manager.addSubtask(epic, subtask2), "Должно быть исключение");
    }

    @Test
    void testShouldSaveAgainTaskIfUpdate() {
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 9, 15, 30);
        Duration duration = Duration.ofMinutes(30);
        Task task = new Task("Task_1", "Task_description_1", startTime, duration);
        Task taskToCollision = new Task("Task_2", "Task_description_2", startTime.plus(duration), duration);

        Task createdTask = manager.addTask(task);
        Task taskToUpdate = new Task(createdTask.getId(), "Task_3", "Task_description_3", startTime.minus(duration), duration);
        manager.addTask(taskToCollision);
        manager.updateTask(taskToUpdate);

        Assertions.assertEquals(2, manager.getPrioritizedTasks().size());
    }
}

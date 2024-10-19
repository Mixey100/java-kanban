import exceptions.ManagerNotAddTaskException;
import exceptions.ManagerValidateException;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

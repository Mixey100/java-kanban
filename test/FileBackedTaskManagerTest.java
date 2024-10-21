import managers.FileBackedTaskManager;
import managers.TaskManagerTest;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file;

    @Override
    protected FileBackedTaskManager newManager() throws IOException {
        file = File.createTempFile("manager", "tmp");
        return new FileBackedTaskManager(file);
    }

    @Test
    public void testSaveAndLoadEmptyFile() {
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getSubtasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadTasks() throws IOException {
        Task task = new Task("Task", "Task_description",
                LocalDateTime.of(2024, 10, 9, 15, 30), Duration.ofMinutes(30));
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Stubtask", "Subtask_description",
                LocalDateTime.of(2024, 10, 10, 15, 15), Duration.ofMinutes(45));
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_description_2",
                LocalDateTime.of(2024, 10, 10, 17, 30), Duration.ofMinutes(30));

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(epic, subtask);
        manager.addSubtask(epic, subtask2);

        FileBackedTaskManager backedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTasks(), backedManager.getTasks());
        assertEquals(manager.getEpics(), backedManager.getEpics());
        assertEquals(manager.getSubtasks(), backedManager.getSubtasks());
    }
}

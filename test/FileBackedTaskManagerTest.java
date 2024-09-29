import managers.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File file = new File("taskmanager.txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.save();

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(file);
        FileBackedTaskManager.loadFromFile(new File("taskmanager.txt"));
        assertTrue(loadedManager.getSubtasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadTasks() throws IOException {
        File temp = File.createTempFile("taskmanager", ".tmp");
        FileBackedTaskManager manager = new FileBackedTaskManager(temp);
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description");

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(epic, subtask);

        FileBackedTaskManager backedManager = FileBackedTaskManager.loadFromFile(temp);
        assertEquals(manager.getTasks(), backedManager.getTasks());
        assertEquals(manager.getEpics(), backedManager.getEpics());
        assertEquals(manager.getSubtasks(), backedManager.getSubtasks());
    }
}

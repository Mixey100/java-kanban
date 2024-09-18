import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void init() {
        manager = Managers.getDefault();
    }

    @Test
    void addTask_shouldSaveTask() {
        Task task = new Task("tasks.Task", "Task_description");
        Task expectedTask = new Task("tasks.Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertNotNull(actualTask);
        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedId() {
        Task task = new Task("tasks.Task", "Task_description");
        Task savedTask = manager.addTask(task);
        Task updatedTask = new Task(savedTask.getId(), "Task_updated", "Task_description_updated");
        Task expectedUpdatedTask = new Task(savedTask.getId(), "Task_updated", "Task_description_updated");

        Task actualUpdatedTask = manager.updateTask(updatedTask);

        Assertions.assertEquals(expectedUpdatedTask, actualUpdatedTask);
    }

    @Test
    void shouldSaveTaskAndFindById() {
        Task task = new Task("tasks.Task", "Task_description");
        Task savedTask = manager.addTask(task);
        Task expectedTask = task;

        Task actualTask = manager.getTaskById(savedTask.getId());

        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void addTask_checkConflictId() {
        Task task = new Task(10, "tasks.Task", "Task_description");
        Task expectedTask = new Task(0, "tasks.Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void addTask_checkConstFields() {
        Task task = new Task("tasks.Task", "Task_description");
        Task expectedTask = new Task("tasks.Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertEquals(expectedTask, actualTask);
        Assertions.assertEquals(expectedTask.getName(), actualTask.getName());
        Assertions.assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        Assertions.assertEquals(expectedTask.getStatus(), actualTask.getStatus());
        Assertions.assertEquals(expectedTask.getId(), actualTask.getId());
    }

    @Test
    void checkAddTaskDifferentTypeAndFindById() {
        Task task = new Task("tasks.Task", "Task_description");
        Epic epic = new Epic("tasks.Epic", "Epic_description");
        Subtask subtask = new Subtask("tasks.Subtask", "Subtask_description");
        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);
        Subtask savedSubtask = manager.addSubtask(epic, subtask);

        Task expectedTask = new Task(savedTask.getId(), "tasks.Task", "Task_description");
        Epic expectedEpic = new Epic(savedEpic.getId(), "tasks.Epic", "Epic_description");
        Subtask expectedSubtask = new Subtask(savedSubtask.getId(), "tasks.Subtask", "Subtask_description");

        Task actualTask = manager.getTaskById(savedTask.getId());
        Task actualEpic = manager.getEpicById(savedEpic.getId());
        Task actualSubtask = manager.getSubtaskById(savedSubtask.getId());
        Assertions.assertEquals(expectedTask, actualTask);
        Assertions.assertEquals(expectedEpic, actualEpic);
        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }
}

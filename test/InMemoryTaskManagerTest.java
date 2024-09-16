import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void init() {
        manager = Managers.getDefault();
    }

    @Test
    void addTask_shouldSaveTask() {
        Task task = new Task("Task", "Task_description");
        Task expectedTask = new Task("Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertNotNull(actualTask);
        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedId() {
        Task task = new Task("Task", "Task_description");
        Task savedTask = manager.addTask(task);
        Task updatedTask = new Task(savedTask.getId(), "Task_updated", "Task_description_updated");
        Task expectedUpdatedTask = new Task(savedTask.getId(), "Task_updated", "Task_description_updated");

        Task actualUpdatedTask = manager.updateTask(updatedTask);

        Assertions.assertEquals(expectedUpdatedTask, actualUpdatedTask);
    }

    @Test
    void shouldSaveTaskAndFindById() {
        Task task = new Task("Task", "Task_description");
        Task savedTask = manager.addTask(task);
        Task expectedTask = task;

        Task actualTask = manager.getTaskById(savedTask.getId());

        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void addTask_checkConflictId() {
        Task task = new Task(10, "Task", "Task_description");
        Task expectedTask = new Task(0, "Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertEquals(expectedTask, actualTask);
    }

    @Test
    void addTask_checkConstFields() {
        Task task = new Task("Task", "Task_description");
        Task expectedTask = new Task("Task", "Task_description");

        Task actualTask = manager.addTask(task);

        Assertions.assertEquals(expectedTask, actualTask);
        Assertions.assertEquals(expectedTask.getName(), actualTask.getName());
        Assertions.assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        Assertions.assertEquals(expectedTask.getStatus(), actualTask.getStatus());
        Assertions.assertEquals(expectedTask.getId(), actualTask.getId());
    }

    @Test
    void checkAddTaskDifferentTypeAndFindById() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description");
        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);
        Subtask savedSubtask = manager.addSubtask(epic, subtask);

        Task expectedTask = new Task(savedTask.getId(), "Task", "Task_description");
        Epic expectedEpic = new Epic(savedEpic.getId(), "Epic", "Epic_description");
        Subtask expectedSubtask = new Subtask(savedSubtask.getId(), "Subtask", "Subtask_description");

        Task actualTask = manager.getTaskById(savedTask.getId());
        Task actualEpic = manager.getEpicById(savedEpic.getId());
        Task actualSubtask = manager.getSubtaskById(savedSubtask.getId());
        Assertions.assertEquals(expectedTask, actualTask);
        Assertions.assertEquals(expectedEpic, actualEpic);
        Assertions.assertEquals(expectedSubtask, actualSubtask);
    }
    /**
     * Тест прорверяет удаление задач из менеджера и истории по Id, при удалении эпика должны удаляться его субтаски
     **/
    @Test
    void checkDeleteTasksByIdFromTaskManagerAndHistory() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description");
        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);
        Subtask savedSubtask = manager.addSubtask(epic, subtask);

        manager.getTaskById(savedTask.getId());
        manager.getEpicById(savedEpic.getId());
        manager.getSubtaskById(savedSubtask.getId());

        manager.deleteTaskById(savedTask.getId());
        manager.deleteEpicById(savedEpic.getId());

        List<Task> tasks = manager.getTasks();
        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();
        List<Subtask> subtasksById = epic.getSubtasksList();
        List<Task> viewHistory = manager.getHistoryList();

        assertEquals(0, tasks.size());
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasksById.size());
        assertEquals(0, viewHistory.size());
    }

    /**
     * Тест прорверяет удаление всех задач из менеджера и истории по Id, при удалении эпиков должны удаляться все субтаски
     **/
    @Test
    void checkDeleteAllTasksFromTaskManagerAndHistory() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description");
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_description_2");
        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);
        Subtask savedSubtask = manager.addSubtask(epic, subtask);
        Subtask savedSubtask2 = manager.addSubtask(epic, subtask2);

        manager.getTaskById(savedTask.getId());
        manager.getEpicById(savedEpic.getId());
        manager.getSubtaskById(savedSubtask.getId());
        manager.getSubtaskById(savedSubtask2.getId());

        manager.deleteTasks();
        manager.deleteEpics();

        List<Task> tasks = manager.getTasks();
        List<Epic> epics = manager.getEpics();
        List<Subtask> subtasks = manager.getSubtasks();
        List<Task> viewHistory = manager.getHistoryList();

        assertEquals(0, tasks.size());
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
        assertEquals(0, viewHistory.size());
    }
}

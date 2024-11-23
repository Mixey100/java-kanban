import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;
import tasks.Subtask;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void testShouldAddTaskInHistory() {
        Task task = new Task("Task", "Task_description");
        Task savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());

        final List<Task> history = manager.getHistoryList();
        assertTrue(history != null && !history.isEmpty(), "История пустая.");
        assertEquals(1, history.size(), "Больше чем 1 элемент в истории");
        assertEquals(task, history.get(0), "История не пустая.");
    }

    @Test
    void testCheckHistoryAddingOnTasksGet() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");

        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(savedTask);
        expectedList.add(savedEpic);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());

        List<Task> actualList = manager.getHistoryList();
        Assertions.assertEquals(expectedList, actualList);
    }

    /**
     * Тест прорверяет, что  задача, после второго прочтения сохранится в конце истории
     * и удалит предыдущую
     **/
    @Test
    public void testCheckThatOldTaskWontBeRemovedOnAddingNew() {
        Task task = new Task("Task", "Task_description");
        Task savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());

        savedTask.setName("Task_2");
        savedTask.setDescription("Task_2_description");
        savedTask.setStatus(Status.DONE);

        manager.updateTask(savedTask);
        manager.getTaskById(savedTask.getId());

        List<Task> viewHistory = manager.getHistoryList();
        assertEquals(1, viewHistory.size());

        assertEquals("Task_2", viewHistory.get(0).getName());
        assertEquals("Task_2_description", viewHistory.get(0).getDescription());
        assertEquals(Status.DONE, viewHistory.get(0).getStatus());
    }

    /**
     * Тест прорверяет удаление задачи из истории в начале, в середине и в конце, а также удаление подзадачи при
     * удаленнии эпика
     **/
    @Test
    public void testCheckDeleteTaskInHistory() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", epic);
        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);
        Subtask savedSubtask = manager.addSubtask(subtask);

        manager.getTaskById(savedTask.getId());
        manager.getEpicById(savedEpic.getId());
        manager.deleteTaskById(savedTask.getId());

        List<Task> viewHistory = manager.getHistoryList();
        assertEquals(1, viewHistory.size());
        assertEquals(epic, viewHistory.get(0));

        savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());
        manager.getSubtaskById(savedSubtask.getId());
        manager.deleteTaskById(savedTask.getId());

        viewHistory = manager.getHistoryList();
        assertEquals(2, viewHistory.size());
        assertEquals(epic, viewHistory.get(0));
        assertEquals(subtask, viewHistory.get(1));

        savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());
        manager.deleteTaskById(savedTask.getId());

        viewHistory = manager.getHistoryList();
        assertEquals(2, viewHistory.size());
        assertEquals(epic, viewHistory.get(0));
        assertEquals(subtask, viewHistory.get(1));

        manager.deleteEpicById(savedEpic.getId());
        viewHistory = manager.getHistoryList();
        assertEquals(0, viewHistory.size());

    }
}
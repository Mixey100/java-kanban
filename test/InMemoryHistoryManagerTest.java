import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

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
    void add_shouldAddTaskInHistory() {
        Task task = new Task("tasks.Task", "Task_description");
        Task savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());

        final List<Task> history = manager.getHistoryList();
        assertTrue(history != null && !history.isEmpty(), "История пустая.");
        assertEquals(1, history.size(), "Больше чем 1 элемент в истории");
        assertEquals(task, history.get(0), "История не пустая.");
    }

    @Test
    void add_checkHistoryAddingOnTasksGet() {
        Task task = new Task("tasks.Task", "Task_description");
        Epic epic = new Epic("tasks.Epic", "Epic_description");

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
    /*Тест прорверяет, что измененная и ранее прочитанная задача, после второго прочтения сохранит в истории
     * состояние первой задачи*/
    @Test
    public void CheckThatOldTaskWontBeRemovedOnAddingNew() {
        Task task = new Task("tasks.Task", "Task_description");
        Task savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());
        savedTask.setName("Task_2");
        savedTask.setDescription("Task_2_description");
        savedTask.setStatus(Status.DONE);

        manager.updateTask(savedTask);
        manager.getTaskById(savedTask.getId());

        List<Task> viewHistory = manager.getHistoryList();
        assertEquals(2, viewHistory.size());
        assertEquals("tasks.Task", viewHistory.get(0).getName());
        assertEquals("Task_description", viewHistory.get(0).getDescription());
        assertEquals(Status.NEW, viewHistory.get(0).getStatus());
        assertEquals("Task_2", viewHistory.get(1).getName());
        assertEquals("Task_2_description", viewHistory.get(1).getDescription());
        assertEquals(Status.DONE, viewHistory.get(1).getStatus());
    }
}
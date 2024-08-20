import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        historyManager = manager.getHistoryManager();
    }

    @Test
    void add_shouldAddTaskInHistory() {
        Task task = new Task("Task", "Task_description");
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertTrue(history != null && !history.isEmpty(), "История пустая.");
        assertEquals(1, history.size(), "Больше чем 1 элемент в истории");
        assertEquals(task, history.get(0), "История не пустая.");
    }

    @Test
    void add_checkHistoryAddingOnTasksGet() {
        Task task = new Task("Task", "Task_description");
        Epic epic = new Epic("Epic", "Epic_description");

        Task savedTask = manager.addTask(task);
        Epic savedEpic = manager.addEpic(epic);

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(savedTask);
        expectedList.add(savedEpic);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());

        List<Task> actualList = historyManager.getHistory();
        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    public void checkThatOldTaskSavePreviousFields() {
        Task task = new Task("Task", "Task_description");
        Task savedTask = manager.addTask(task);
        manager.getTaskById(savedTask.getId());
        savedTask.setName("Task_2");

        manager.updateTask(savedTask);
        manager.getTaskById(savedTask.getId());

        List<Task> viewHistory = manager.getHistoryManager().getHistory();
        assertEquals(2, viewHistory.size());
        assertEquals("Task", viewHistory.get(0).getName());
        assertEquals("Task_2", viewHistory.get(1).getName());
    }
}
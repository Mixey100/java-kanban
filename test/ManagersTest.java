import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefault_shouldReturnTaskManagerAndHistoryManagerNotNull() {
        TaskManager manager = Managers.getDefault();

        Assertions.assertNotNull(manager, "TaskManager null");
        Assertions.assertNotNull(manager.getHistoryList(), "HistoryManager null");


    }
}
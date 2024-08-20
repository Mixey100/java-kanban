import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void getDefault_shouldReturnTaskManagerAndHistoryManagerNotNull() {

        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = manager.getHistoryManager();

        Assertions.assertNotNull(manager, "TaskManager null");
        Assertions.assertNotNull(historyManager, "HistoryManager null");


    }
}
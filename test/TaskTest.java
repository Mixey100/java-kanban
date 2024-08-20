import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldEqualsTaskIfEqualsId() {

        Task task1 = new Task("Task1", "Task1_description");
        Task task2 = new Task("Task2", "Task2_description");

        Assertions.assertEquals(task1.getId(), task2.getId());
        Assertions.assertEquals(task1, task2, "Экземпляры класса равны");
    }
}
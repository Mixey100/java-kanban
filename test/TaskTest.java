import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

class TaskTest {

    @Test
    void shouldEqualsTaskIfEqualsId() {

        Task task1 = new Task(0,"Task1", "Task1_description");
        Task task2 = new Task(0,"Task2", "Task2_description");

        Assertions.assertEquals(task1.getId(), task2.getId());
        Assertions.assertEquals(task1, task2, "Экземпляры класса равны");
    }
}
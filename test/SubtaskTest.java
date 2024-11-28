import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;


class SubtaskTest {

    @Test
    void shouldEqualsSubtaskIfEqualsId() {

        Epic epic = new Epic(0, "Epic", "Epic_description");
        Subtask subtask1 = new Subtask(1, "Subtask1", "Subtask1_description", epic);
        Subtask subtask2 = new Subtask(1, "Subtask2", "Subtask2_description", epic);

        Assertions.assertEquals(subtask1.getId(), subtask2.getId());
        Assertions.assertEquals(subtask1, subtask2, "Экземпляры класса равны");
    }

}
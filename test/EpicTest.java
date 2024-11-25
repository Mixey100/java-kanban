import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;


class EpicTest {

    @Test
    void testShouldEqualsEpicIfEqualsId() {

        Epic epic1 = new Epic(0, "Epic1", "Epic1_description");
        Epic epic2 = new Epic(0, "Epic2", "Epic2_description");

        Assertions.assertEquals(epic1.getId(), epic2.getId());
        Assertions.assertEquals(epic1, epic2, "Экземпляры класса не равны");
    }

    @Test
    void testShouldChangeEpicStatusIfChangedSubtaskStatus() {
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask1 = new Subtask(1, "Subtask_1", "Subtask_description_1", epic);
        Subtask subtask2 = new Subtask(2, "Subtask_2", "Subtask_description_2", epic);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        Assertions.assertEquals(Status.DONE, epic.getStatus());

        epic.removeSubtasks();
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testShouldDetermineTimeEpic() {
        Epic epic = new Epic(0, "Epic", "Epic_description");
        Subtask subtask1 = new Subtask(1, "Stubtask", "Subtask_description",
                LocalDateTime.of(2024, 10, 10, 15, 15), Duration.ofMinutes(45), epic);
        Subtask subtask2 = new Subtask(2, "Subtask_2", "Subtask_description_2",
                LocalDateTime.of(2024, 10, 10, 17, 30), Duration.ofMinutes(30), epic);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 15, 15), epic.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 17, 30).plus(Duration.ofMinutes(30)), epic.getEndTime());
        Assertions.assertEquals(Duration.ofMinutes(75), epic.getDuration());

        epic.removeSubtasks();

        Assertions.assertNull(epic.getStartTime());
        Assertions.assertNull(epic.getEndTime());
        Assertions.assertNull(epic.getDuration());
    }
}
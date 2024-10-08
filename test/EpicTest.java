import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.util.*;


class EpicTest {

    @Test
    void shouldEqualsEpicIfEqualsId() {

        Epic epic1 = new Epic("Epic1", "Epic1_description");
        Epic epic2 = new Epic("Epic2", "Epic2_description");

        Assertions.assertEquals(epic1.getId(), epic2.getId());
        Assertions.assertEquals(epic1, epic2, "Экземпляры класса не равны");
    }

    @Test
    void shouldChangeEpicStatusIfChangedSubtaskStatus() {
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask1 = new Subtask(1,"Subtask_1", "Subtask_description_1");
        Subtask subtask2 = new Subtask(2,"Subtask_2", "Subtask_description_2");
        subtask1.setEpic(epic);
        subtask2.setEpic(epic);

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
}
package tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtasksByEpic = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtasksByEpic.put(subtask.getId(), subtask);
        subtask.setEpic(this);
        recomputeStatus();
    }

    public void removeSubtasks() {
        subtasksByEpic.clear();
        recomputeStatus();
    }

    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasksByEpic.values());
    }

    public void removeSubtask(int id) {
        subtasksByEpic.remove(id);
        recomputeStatus();
    }

    public Map<Integer, Subtask> getSubtasksByEpic() {
        return subtasksByEpic;
    }

    public void recomputeStatus() {
        List<Subtask> subtasks = getSubtasksList();
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        int doneCount = 0;
        int newCount = 0;
        for (Subtask subtask : subtasks) {
            Status status = subtask.getStatus();
            if (status == Status.DONE) {
                doneCount++;
            } else if (status == Status.NEW) {
                newCount++;
            }
        }
        if (doneCount == subtasks.size()) {
            setStatus(Status.DONE);
        } else if (newCount == subtasks.size()) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Epic getClone() {
        Epic cloneEpic = new Epic(getId(), getName(), getDescription());
        cloneEpic.setStatus(getStatus());
        cloneEpic.setId(getId());
        return cloneEpic;
    }

   @Override
    public String toString() {
        return "tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
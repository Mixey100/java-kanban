import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private Map<Integer, Subtask> subtasksByEpic = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public void addSubtask(Subtask subtask) {
        subtasksByEpic.put(subtask.getId(), subtask);
        recomputeStatus();
    }

    public void removeSubtasks() {
        subtasksByEpic.clear();
    }

    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasksByEpic.values());
    }

    public void removeSubtask(int id) {
        subtasksByEpic.remove(id);
    }

    public void recomputeStatus() {
        ArrayList<Subtask> subtasks = new ArrayList<>(getSubtasksList());

        if (subtasks.size() == 1) {
            setStatus(subtasks.getFirst().getStatus());
        } else if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
        } else {
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
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}

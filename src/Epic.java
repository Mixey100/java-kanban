import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {

    private Map<Integer, Subtask> subtasksByEpic = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public void addSubtask(int id, Subtask subtask) {
        subtasksByEpic.put(id, subtask);
    }

    public void removeSubtasks() {
        subtasksByEpic.clear();
    }

    public List<Subtask> getSubtasksList() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtasksByEpic.values()) {
            subtasks.add(subtask);
        }
        return subtasks;
    }

    public void removeSubtask(int id) {
        subtasksByEpic.remove(id);
    }

    public void recomputeStatus() {
        ArrayList<Subtask> subtasks = new ArrayList<>(getSubtasksList());

        if (subtasks.size() == 1) {
            setStatus(subtasks.get(0).getStatus());
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

    public Map<Integer, Subtask> getSubtasksByEpic() {
        return subtasksByEpic;
    }

    public void setSubtasksByEpic(Map<Integer, Subtask> subtasksByEpic) {
        this.subtasksByEpic = subtasksByEpic;
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

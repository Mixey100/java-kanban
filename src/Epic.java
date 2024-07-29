import java.util.HashMap;
import java.util.Map;

public class Epic extends Task{

    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Map<Integer, Subtask> subtasks) {
        this.subtasks = subtasks;
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

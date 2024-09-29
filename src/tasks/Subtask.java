package tasks;

public class Subtask extends Task {

    private Epic epic;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(int id, String name, String description) {
        super(id, name, description);
    }

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        epic.recomputeStatus();
    }

    @Override
    public Subtask getClone() {
        Subtask cloneSubtask = new Subtask(getName(), getDescription());
        cloneSubtask.setEpic(getEpic());
        cloneSubtask.setStatus(getStatus());
        cloneSubtask.setId(getId());
        return cloneSubtask;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}

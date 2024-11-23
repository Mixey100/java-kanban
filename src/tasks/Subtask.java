package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private transient Epic epic;
    private Integer epicId;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.epicId = epic.getId();
        setType(TaskType.SUBTASK);
    }

    public Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
        this.epicId = epic.getId();
        setType(TaskType.SUBTASK);
    }

    public Subtask(int id, String name, String description, Status status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
        this.epicId = epic.getId();
        setType(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, LocalDateTime startTime, Duration duration, Epic epic) {
        super(name, description, startTime, duration);
        this.epic = epic;
        this.epicId = epic.getId();
        setType(TaskType.SUBTASK);
    }

    public Subtask(int id, String name, String description, LocalDateTime startTime, Duration duration, Epic epic) {
        super(id, name, description, startTime, duration);
        this.epic = epic;
        this.epicId = epic.getId();
        setType(TaskType.SUBTASK);
    }

    public Subtask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        setType(TaskType.SUBTASK);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        epic.recomputeStatus();
    }

    @Override
    public Subtask getClone() {
        Subtask cloneSubtask = new Subtask(getName(), getDescription(), getEpic());
        cloneSubtask.setStatus(getStatus());
        cloneSubtask.setId(getId());
        cloneSubtask.setStartTime(getStartTime());
        cloneSubtask.setDuration(getDuration());
        return cloneSubtask;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}

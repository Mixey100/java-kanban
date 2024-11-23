package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtasksByEpic = new HashMap<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        setType(TaskType.EPIC);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        setType(TaskType.EPIC);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        setType(TaskType.EPIC);
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        setType(TaskType.EPIC);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        setType(TaskType.EPIC);
    }

    public void addSubtask(Subtask subtask) {
        subtasksByEpic.put(subtask.getId(), subtask);
        subtask.setEpic(this);
        recomputeStatus();
        calculateTime();
    }

    public void removeSubtasks() {
        subtasksByEpic.clear();
        recomputeStatus();
        calculateTime();
    }

    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasksByEpic.values());
    }

    public void removeSubtask(int id) {
        subtasksByEpic.remove(id);
        recomputeStatus();
        calculateTime();
    }

    void recomputeStatus() {
        if (getSubtasksList().isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        int doneCount = 0;
        int newCount = 0;
        for (Subtask subtask : getSubtasksList()) {
            Status status = subtask.getStatus();
            if (status == Status.DONE) {
                doneCount++;
            } else if (status == Status.NEW) {
                newCount++;
            }
        }
        if (doneCount == getSubtasksList().size()) {
            setStatus(Status.DONE);
        } else if (newCount == getSubtasksList().size()) {
            setStatus(Status.NEW);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Epic getClone() {
        Epic cloneEpic = new Epic(getName(), getDescription());
        cloneEpic.setStatus(getStatus());
        cloneEpic.setId(getId());
        cloneEpic.setStartTime(getStartTime());
        cloneEpic.setDuration(getDuration());
        return cloneEpic;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    private void calculateTime() {
        if (!getSubtasksList().isEmpty()) {
            Duration durationEpic = Duration.ofMinutes(0);
            for (Subtask subtask : getSubtasksList()) {
                if (subtask.startTime == null || subtask.duration == null) {
                    continue;
                }
                if (startTime == null || subtask.startTime.isBefore(startTime)) {
                    startTime = subtask.startTime;
                }
                if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
                durationEpic = durationEpic.plus(subtask.duration);
            }
            duration = durationEpic;
        } else {
            duration = null;
            startTime = null;
            endTime = null;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}

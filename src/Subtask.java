public class Subtask extends Task{

    private Epic epic;

    public Subtask(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        epic.recomputeStatus();
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

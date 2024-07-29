import java.util.*;

public class TaskManager {

    private static int id;
    private static final Map<Integer, Task> tasksMap = new HashMap<>();
    private static final Map<Integer, Epic> epicsMap = new HashMap<>();
    private static final Map<Integer, Subtask> subtasksMap = new HashMap<>();

    public Task createAndSaveTask(String name, String description) {
        int id = getId();
        Task task = new Task(name, description, id);
        tasksMap.put(id, task);
        return task;
    }

    public Epic createAndSaveEpic(String name, String description) {
        int id = getId();
        Epic epic = new Epic(name, description, id);
        epicsMap.put(id, epic);
        return epic;
    }

    public Subtask createAndSaveSubtask(Epic epic, String name, String description) {
        int id = getId();
        Subtask subtask = new Subtask(name, description, id);
        subtasksMap.put(id, subtask);
        epic.getSubtasks().put(id, subtask);
        subtask.setEpic(epic);
        return subtask;
    }

    public static int getId() {
        return id++;
    }

    public static Map<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public static Map<Integer, Epic> getEpicsMap() {
        return epicsMap;
    }

    public static Map<Integer, Subtask> getSubtasksMap() {
        return subtasksMap;
    }

    public void deleteTasks() {
        tasksMap.clear();
    }

    public void deleteEpics() {
        for (Epic epic : epicsMap.values()) {
            epic.getSubtasks().clear();
        }
        epicsMap.clear();
        subtasksMap.clear();

    }

    public void deleteSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.getSubtasks().clear();
        }
        subtasksMap.clear();
    }

    public Task getTaskById(int id) {
        return tasksMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasksMap.get(id);
    }

    public void updateTask(Task task, Status status) {
        task.setStatus(status);
    }

    private void updateEpic(Epic epic) {
        ArrayList<Subtask> subtasks = new ArrayList<>(epic.getSubtasks().values());
        if (subtasks.size() == 1) {
            epic.setStatus(subtasks.get(0).getStatus());
        } else {
            for (int i = 1; i < subtasks.size(); i++) {
                if (subtasks.get(i - 1).getStatus() == subtasks.get(i).getStatus() && subtasks.get(i - 1).getStatus() == Status.DONE) {
                    epic.setStatus(Status.DONE);
                } else if (subtasks.get(i - 1).getStatus() == subtasks.get(i).getStatus() && subtasks.get(i - 1).getStatus() == Status.NEW) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        }
    }

    public void updateSubtask(Subtask subtask, Status status) {
        subtask.setStatus(status);
        updateEpic(subtask.getEpic());
    }

    public void deleteTaskById(int id) {
        tasksMap.remove(id);
    }

    public void deleteEpicById(int id) {
        for (Subtask subtask : epicsMap.get(id).getSubtasks().values()) {
            subtasksMap.remove(subtask.getId());
        }
        epicsMap.get(id).getSubtasks().clear();
        epicsMap.remove(id);

    }

    public void deleteSubtaskById(int id) {
        subtasksMap.get(id).getEpic().getSubtasks().remove(id);
        subtasksMap.remove(id);
    }

    public Map<Integer, Subtask> getSybtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

}

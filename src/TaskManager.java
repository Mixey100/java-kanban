import java.util.*;

public class TaskManager {

    private int id;
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();

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
        epic.addSubtask(subtask);
        return subtask;
    }

    private int getId() {
        return id++;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    public void deleteTasks() {
        tasksMap.clear();
    }

    public void deleteEpics() {
        epicsMap.clear();
        subtasksMap.clear();

    }

    public void deleteSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.removeSubtasks();
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

    public Task updateTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasksMap.containsKey(taskId)) {
            tasksMap.put(taskId, newTask);
            return newTask;
        } else {
            System.out.println("Задачи с таким номером не существует.");
            return null;
        }
    }

    public Epic updateEpic(Epic newEpic) {
        int epicId = newEpic.getId();
        Epic epic = epicsMap.get(epicId);
        if (epicsMap.containsKey(epicId)) {
            epic.setName(newEpic.getName());
            epic.setDescription(newEpic.getDescription());
        } else {
            System.out.println("Эпика с таким номером не существует.");
            return null;
        }
        return epic;
    }

    public Subtask updateSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (subtasksMap.containsKey(subtaskId)) {
            subtasksMap.put(subtaskId, newSubtask);
            Epic epic = newSubtask.getEpic();
            epic.addSubtask(newSubtask);
            return newSubtask;
        } else {
            System.out.println("Подзадачи с таким номером не существует.");
            return null;
        }
    }

    public void deleteTaskById(int id) {
        tasksMap.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epicsMap.get(id);
        for (Subtask subtask : epic.getSubtasksList()) {
            subtasksMap.remove(subtask.getId());
        }
        epicsMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(id);
        subtasksMap.remove(id);
    }

    public List<Subtask> getSybtasksByEpic(Epic epic) {
        return epic.getSubtasksList();
    }

}

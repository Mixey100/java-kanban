import java.util.*;

public class TaskManager {

    private static int id;
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
        epic.addSubtask(id, subtask);
        subtask.setEpic(epic);
        return subtask;
    }

    public static int getId() {
        return id++;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Task task : tasksMap.values()) {
            tasks.add(task);
        }
        return tasks;
    }

    public List<Epic> getEpics() {
        List<Epic> epics = new ArrayList<>();
        for (Epic epic : epicsMap.values()) {
            epics.add(epic);
        }
        return epics;
    }

    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtasksMap.values()) {
            subtasks.add(subtask);
        }
        return subtasks;
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
        if (tasksMap.containsKey(epicId)) {
            tasksMap.put(epicId, newEpic);
            return newEpic;
        } else {
            System.out.println("Эпика с таким номером не существует.");
            return null;
        }
    }

    public Subtask updateSubtask(Subtask newSubtask) {
        int subtaskId = newSubtask.getId();
        if (subtasksMap.containsKey(subtaskId)) {
            subtasksMap.put(subtaskId, newSubtask);
            Epic epic = newSubtask.getEpic();
            epic.addSubtask(subtaskId, newSubtask);
            epic.recomputeStatus();
            return newSubtask;
        } else {
            System.out.println("Подзадачи с таким номером не существует.");
            return null;
        }
    }

    public Task deleteTaskById(int id) {
        Task task = tasksMap.get(id);
        tasksMap.remove(id);
        return task;
    }

    public Epic deleteEpicById(int id) {
        Epic epic = epicsMap.get(id);
        for (Subtask subtask : epic.getSubtasksList()) {
            subtasksMap.remove(subtask.getId());
        }
        epic.removeSubtasks();
        epicsMap.remove(id);
        return epic;
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

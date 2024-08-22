package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int id;
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task addTask(Task task) {
        int id = getId();
        task.setId(id);
        tasksMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Epic epic, Subtask subtask) {
        int id = getId();
        subtask.setId(id);
        subtasksMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        return subtask;
    }


    private int getId() {
        return id++;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public void deleteTasks() {
        tasksMap.clear();
    }

    @Override
    public void deleteEpics() {
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.removeSubtasks();
        }
        subtasksMap.clear();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void deleteTaskById(int id) {
        tasksMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicsMap.get(id);
        for (Subtask subtask : epic.getSubtasksList()) {
            subtasksMap.remove(subtask.getId());
        }
        epicsMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(id);
        subtasksMap.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasksList();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        if (Objects.nonNull(task)){
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        if (Objects.nonNull(epic)) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        if (Objects.nonNull(subtask)) {
            historyManager.add(subtask);
        }
        return subtask;
    }
    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }
}

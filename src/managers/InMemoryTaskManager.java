package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
        for (int id : tasksMap.keySet()) {
            historyManager.remove(id);
        }
        tasksMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epicsMap.keySet()) {
            Epic epic = epicsMap.get(id);
            epic.removeSubtasks();
            historyManager.remove(id);
        }
        for (int id : subtasksMap.keySet()) {
            historyManager.remove(id);
        }
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.removeSubtasks();
        }
        for (int id : subtasksMap.keySet()) {
            historyManager.remove(id);
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
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicsMap.get(id);
        for (Subtask subtask : getSubtasksByEpic(epic)) {
            int subtaskId = subtask.getId();
            subtasksMap.remove(subtaskId);
            historyManager.remove(subtaskId);

        }
        epicsMap.remove(id);
        historyManager.remove(id);
        epic.removeSubtasks();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(id);
        subtasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasksList();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        if (Objects.nonNull(task)) {
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
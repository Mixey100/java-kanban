package managers;

import exceptions.ManagerValidateException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    @Override
    public Task addTask(Task task) {
        int id = getId();
        task.setId(id);
        tasksMap.put(task.getId(), task);
        addToPrioritizedTasks(task);
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
        addToPrioritizedTasks(subtask);
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
        for (Task task : tasksMap.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasksMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epicsMap.values()) {
            for (Subtask subtask : epic.getSubtasksList()) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(epic.getId());
        }
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epicsMap.values()) {
            epic.removeSubtasks();
        }
        for (Subtask subtask : subtasksMap.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasksMap.clear();
    }

    @Override
    public Task updateTask(Task newTask) {
        int taskId = newTask.getId();
        if (tasksMap.containsKey(taskId)) {
            prioritizedTasks.remove(tasksMap.get(newTask.getId()));
            for (Task task : prioritizedTasks) {
                if (notIntersected(newTask, task)) {
                    addToPrioritizedTasks(newTask);
                } else {
                    prioritizedTasks.add(tasksMap.get(newTask.getId()));
                }
            }
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
            prioritizedTasks.remove(subtasksMap.get(newSubtask.getId()));
            for (Task task : prioritizedTasks) {
                if (notIntersected(newSubtask, task)) {
                    addToPrioritizedTasks(newSubtask);
                } else {
                    prioritizedTasks.add(tasksMap.get(newSubtask.getId()));
                }
            }
            Epic epic = newSubtask.getEpic();
            epic.addSubtask(newSubtask);
            subtasksMap.put(subtaskId, newSubtask);
            return newSubtask;
        } else {
            System.out.println("Подзадачи с таким номером не существует.");
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasksMap.get(id));
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
            prioritizedTasks.remove(subtask);

        }
        epicsMap.remove(id);
        historyManager.remove(id);
        epic.removeSubtasks();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);
        prioritizedTasks.remove(subtask);
        Epic epic = subtask.getEpic();
        epic.removeSubtask(id);
        subtasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasksList();
        return subtasks.stream().toList();
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void checkCrossTasks(Task newTask) {
        prioritizedTasks.stream()
                .dropWhile(task -> notIntersected(newTask, task))
                .findFirst()
                .ifPresent(collisionTask -> {
                    throw new ManagerValidateException("Задача %s пересекается с задачей: %s".formatted(collisionTask, newTask));
                });
    }

    private static boolean notIntersected(Task newTask, Task task) {
        return !newTask.getStartTime().isBefore(task.getEndTime()) || !newTask.getEndTime().isAfter(task.getStartTime());
    }

    private void addToPrioritizedTasks(Task task) {
        if (!task.isScheduled()) {
            System.out.println("Задача не имеет временных параметров");
            return;
        }
        checkCrossTasks(task);
        prioritizedTasks.add(task);
    }
}


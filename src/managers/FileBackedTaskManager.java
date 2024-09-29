package managers;

import tasks.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    private String toString(Task task) {
        String str = null;
        switch (task.getType()) {
            case TASK:
            case EPIC:
                str = String.format("%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription());
                break;
            case SUBTASK:
                str = String.format("%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                        task.getStatus(), task.getDescription(), ((Subtask) task).getEpic().getId());
        }
        return str;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String str = null;
            boolean isTitle = true;
            while ((str = reader.readLine()) != null) {
                if (isTitle) {
                    isTitle = false;
                    continue;
                }
                manager.fromString(str);
            }
            return manager;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения");
        }
    }

    private void fromString(String value) {
        String[] values = value.split(",");
        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        String status = values[3];
        String description = values[4];
        if (id > this.id) {
            this.id = id;
        }
        switch (TaskType.valueOf(type)) {
            case TASK:
                Task task = new Task(id, name, description, Status.valueOf(status));
                tasksMap.put(id, task);
                break;
            case EPIC:
                Epic epic = new Epic(id, name, description, Status.valueOf(status));
                epicsMap.put(id, epic);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(values[5]);
                Subtask subtask = new Subtask(id, name, description, Status.valueOf(status));
                subtasksMap.put(id, subtask);
                subtask.setEpic(epicsMap.get(epicId));
                Epic subtasksEpic = epicsMap.get(epicId);
                subtasksEpic.getSubtasksByEpic().put(id, subtask);
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Epic epic, Subtask subtask) {
        super.addSubtask(epic, subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
        return newSubtask;
    }

    @Override
    public void deleteTaskById(int ig) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }
}

package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerValidateException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/tasks", path)) {
                switch (method) {
                    case "GET":
                        String response = gson.toJson(manager.getTasks());
                        sendText(exchange, response, 200);
                        break;
                    case "POST":
                        if (body.isEmpty()) {
                            sendText(exchange, "Тело ответа пустое", 400);
                        } else {
                            addTask(exchange, body);
                        }
                }
            } else if (Pattern.matches("/tasks/\\d+", path)) {
                int id = getId(path);
                if (id != -1) {
                    switch (method) {
                        case "GET":
                            getTask(exchange, id);
                            break;
                        case "DELETE":
                            deleteTask(exchange, id);
                            break;
                        case "POST":
                            if (body.isEmpty()) {
                                sendText(exchange, "Тело ответа пустое", 400);
                            } else {
                                updateTask(exchange, body);
                            }
                    }
                }
            } else {
                sendText(exchange, "Неизвестный запрос", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void addTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = manager.addTask(gson.fromJson(body, Task.class));
            String response = gson.toJson(task);
            sendText(exchange, response, 201);
        } catch (ManagerValidateException exception) {
            sendText(exchange, "Задача пересекается с существующими задачами", 406);
        }
    }

    private void updateTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = manager.updateTask(gson.fromJson(body, Task.class));
            String response = gson.toJson(task);
            sendText(exchange, response, 201);
        } catch (ManagerValidateException exception) {
            sendText(exchange, "Задача пересекается с существующими задачами", 406);
        }
    }

    private void getTask(HttpExchange exchange, int id) {
        try {
            Task task = manager.getTaskById(id);
            if (task != null) {
                String response = gson.toJson(task);
                sendText(exchange, response, 200);
            } else {
                sendText(exchange, "Задачи c id " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void deleteTask(HttpExchange exchange, int id) {
        try {
            Task task = manager.deleteTaskById(id);
            if (Objects.nonNull(task)) {
                sendText(exchange, "Задача c id " + id + " успешно удалена", 200);
            } else {
                sendText(exchange, "Задачи c id " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected int getId(String path) {
        String[] pathPart = path.split("/");
        try {
            return Integer.parseInt(pathPart[2]);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}

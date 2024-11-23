package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerValidateException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

public class SubtaskHandler extends TaskHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/subtasks", path)) {
                switch (method) {
                    case "GET":
                        String response = gson.toJson(manager.getSubtasks());
                        sendText(exchange, response, 200);
                        break;
                    case "POST":
                        if (body.isEmpty()) {
                            sendText(exchange, "Тело ответа пустое", 400);
                        } else {
                            addSubtask(exchange, body);
                        }
                }
            } else if (Pattern.matches("/subtasks/\\d+", path)) {
                int id = getId(path);
                if (id != -1) {
                    switch (method) {
                        case "GET":
                            getSubtask(exchange, id);
                            break;
                        case "DELETE":
                            deleteSubtask(exchange, id);
                            break;
                        case "POST":
                            if (body.isEmpty()) {
                                sendText(exchange, "Тело ответа пустое", 400);
                            } else {
                                updateSubtask(exchange, body);
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

    private void addSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask subTaskFromJSON = restoreSubtaskFromManager(body);
            Subtask subtask = manager.addSubtask(subTaskFromJSON);
            String response = gson.toJson(subtask);
            sendText(exchange, response, 201);
        } catch (ManagerValidateException exception) {
            sendText(exchange, "Подзадача пересекается с существующими задачами", 406);
        }
    }

    private void updateSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask subtaskFromJSON = restoreSubtaskFromManager(body);
            Subtask subtask = manager.updateSubtask(subtaskFromJSON);
            String response = gson.toJson(subtask);
            sendText(exchange, response, 201);
        } catch (ManagerValidateException exception) {
            sendText(exchange, "Подзадача пересекается с существующими задачами", 406);
        }
    }

    private Subtask restoreSubtaskFromManager(String body) {
        Subtask subtaskFromJSON = gson.fromJson(body, Subtask.class);
        Integer epicId = subtaskFromJSON.getEpicId();
        Epic actualEpic = manager.getEpicById(epicId);
        subtaskFromJSON.setEpic(actualEpic);
        return subtaskFromJSON;
    }

    private void getSubtask(HttpExchange exchange, int id) {
        try {
            Subtask subtask = manager.getSubtaskById(id);
            if (Objects.nonNull(subtask)) {
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            } else {
                sendText(exchange, "Подзадачи c id" + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void deleteSubtask(HttpExchange exchange, int id) {
        try {
            Subtask subtask = manager.deleteSubtaskById(id);
            if (Objects.nonNull(subtask)) {
                sendText(exchange, "Подзадача c id " + id + " успешно удалена", 200);
            } else {
                sendText(exchange, "Подзадачи c id " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
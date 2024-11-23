package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/epics", path)) {
                switch (method) {
                    case "GET":
                        String response = gson.toJson(manager.getEpics());
                        sendText(exchange, response, 200);
                        break;
                    case "POST":
                        if (body.isEmpty()) {
                            sendText(exchange, "Тело ответа пустое", 400);
                        } else {
                            addEpic(exchange, body);
                        }
                }
            } else if (Pattern.matches("/epics/\\d+", path)) {
                int id = getId(path);
                if (id != -1) {
                    switch (method) {
                        case "GET":
                            getEpic(exchange, id);
                            break;
                        case "DELETE":
                            deleteEpic(exchange, id);
                            break;
                    }
                }
            } else if (Pattern.matches("/epics/\\d+/subtasks", path)) {
                int id = getId(path);
                if ((id != -1) && (method.equals("GET"))) {
                    getSubtasksByEpic(exchange, id);
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

    private void getSubtasksByEpic(HttpExchange exchange, int id) {
        try {
            Epic epic = manager.getEpicById(id);
            if (Objects.nonNull(epic)) {
                String response = gson.toJson(manager.getSubtasksByEpic(epic));
                sendText(exchange, response, 200);
            } else {
                sendText(exchange, "Эпик c id " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void addEpic(HttpExchange exchange, String body) {
        try {
            Epic epic = manager.addEpic(gson.fromJson(body, Epic.class));
            if (Objects.nonNull(epic)) {
                String response = gson.toJson(epic);
                sendText(exchange, response, 201);
            } else {
                sendText(exchange, "Эпик пустой", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void getEpic(HttpExchange exchange, int id) {
        try {
            Epic epic = manager.getEpicById(id);
            if (Objects.nonNull(epic)) {
                String response = gson.toJson(epic);
                sendText(exchange, response, 200);
            } else {
                sendText(exchange, "Эпика c " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) {
        try {
            Epic Epic = manager.deleteEpicById(id);
            if (Objects.nonNull(Epic)) {
                sendText(exchange, "Эпик c id " + id + " успешно удален", 200);
            } else {
                sendText(exchange, "Эпик c id " + id + " не существует", 404);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}


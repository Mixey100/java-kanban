package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
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
                            Epic epic = gson.fromJson(body, Epic.class);
                            Integer epicId = epic.getId();
                            if (epicId == null) {
                                addEpic(exchange, body);
                            } else {
                                updateEpic(exchange, body);
                            }
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
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при обработке запроса эпика", e);
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
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при получение подзадач эпика", e);
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
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при добавлении эпика", e);
        }
    }

    private void updateEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic epic = manager.updateEpic(gson.fromJson(body, Epic.class));
            String response = gson.toJson(epic);
            sendText(exchange, response, 201);
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при обновлении эпика", e);
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
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при получении эпика", e);
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) {
        try {
            Epic epic = manager.deleteEpicById(id);
            if (Objects.nonNull(epic)) {
                sendText(exchange, "Эпик c id " + id + " успешно удален", 200);
            } else {
                sendText(exchange, "Эпик c id " + id + " не существует", 404);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при удалении эпика", e);
        }
    }
}


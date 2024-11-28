package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.util.logging.Level;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    public void handle(HttpExchange exchange) {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ((Pattern.matches("/history", path)) && (method.equals("GET"))) {
                sendText(exchange, gson.toJson(manager.getHistoryList()), 200);
            } else {
                sendText(exchange, "Неизвестный запрос", 404);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при обработке истории просмотров задач", e);
        }
    }
}

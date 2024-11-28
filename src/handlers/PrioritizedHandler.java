package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;

import java.util.logging.Level;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    public void handle(HttpExchange exchange) {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ((Pattern.matches("/prioritized", path)) && (method.equals("GET"))) {
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
            } else {
                sendText(exchange, "Неизвестный запрос", 404);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
            logger.log(Level.SEVERE, "Ошибка при обработке списка приоритета задач", e);
        }
    }
}

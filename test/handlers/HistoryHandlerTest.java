package handlers;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handle_shouldReturnHistory() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task task2 = new Task("Task_2", "Task_2_description", LocalDateTime.now().plusMinutes(100),
                Duration.ofMinutes(45));
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45), epic);
        manager.addTask(task);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        manager.getTaskById(0);
        manager.getTaskById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(tasks);
        assertEquals(4, tasks.size());
        assertEquals("Task", tasks.get(0).getName());
        assertEquals("Task_2", tasks.get(1).getName());
        assertEquals("Epic", tasks.get(2).getName());
        assertEquals("Subtask", tasks.get(3).getName());
    }

    @Test
    public void handle_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/histor");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}


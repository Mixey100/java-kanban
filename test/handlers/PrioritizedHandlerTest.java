package handlers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import adapters.TaskListTypeToken;
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

class PrioritizedHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handle_shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task task2 = new Task("Task_2", "Task_2_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45));
        Task task3 = new Task("Task_3", "Task_3_description", LocalDateTime.now().plusMinutes(100),
                Duration.ofMinutes(45));
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now().plusMinutes(150),
                Duration.ofMinutes(45), epic);
        manager.addTask(task);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(tasks);
        System.out.println(tasks);
        assertEquals(4, tasks.size());
        assertEquals("Task", tasks.get(0).getName());
        assertEquals("Task_2", tasks.get(1).getName());
        assertEquals("Task_3", tasks.get(2).getName());
        assertEquals("Subtask", tasks.get(3).getName());
    }

    @Test
    public void handle_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/prioritize");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
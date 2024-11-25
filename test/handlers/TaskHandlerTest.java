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

public class TaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testShouldGetTasksList() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task task2 = new Task("Task_2", "Task_2_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45));
        manager.addTask(task);
        manager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("Task", tasks.getFirst().getName());
        assertEquals("Task_2", tasks.get(1).getName());
    }

    @Test
    public void testShouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        String taskJson = gson.toJson(task);
        System.out.println(taskJson);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task", tasks.getFirst().getName());
    }

    @Test
    public void testShouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        manager.addTask(task);

        Task updateTask = new Task(0, "Task_update", "Task_update_description", LocalDateTime.now().plusMinutes(40),
                Duration.ofMinutes(75));
        String taskJson = gson.toJson(updateTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task_update", tasks.getFirst().getName());
    }

    @Test
    public void testShouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task actualTask = manager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task jsonTask = gson.fromJson(response.body(), Task.class);
        assertEquals(actualTask, jsonTask);
    }

    @Test
    public void testShouldRemoveTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        manager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getTasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void testGetTaskShouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTaskShouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testShouldNotAddTaskIntersection() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task task2 = new Task("Task_2", "Task_2_description", LocalDateTime.now(), Duration.ofMinutes(55));
        manager.addTask(task);
        String taskJson = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testShouldNotUpdateTaskIntersection() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Task task2 = new Task("Task_2", "Task_2_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45));
        Task updTask = new Task(0, "Task_update", "Task_update_description", LocalDateTime.now(),
                Duration.ofMinutes(80));

        manager.addTask(task);
        manager.addTask(task2);
        String taskJson = gson.toJson(updTask);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testShouldReturn400WithEmptyBody() throws IOException, InterruptedException {
        String taskJson = "";

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
package handlers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public SubtaskHandlerTest() throws IOException {
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
    public void testShouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);
        Subtask subtask2 = new Subtask("Subtask_2", "Subtask_2_description",
                LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(45), epic);

        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
        assertEquals("Subtask", subtasks.getFirst().getName());
        assertEquals("Subtask_2", subtasks.get(1).getName());
    }

    @Test
    public void testShouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals("Subtask", subtasks.getFirst().getName());
    }

    @Test
    public void testShouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        Subtask updateSubtask = new Subtask(1, "Subtask_update", "Subtask_update_description",
                LocalDateTime.now().plusMinutes(40),
                Duration.ofMinutes(75), epic);
        String subtaskJson = gson.toJson(updateSubtask);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals("Subtask_update", subtasks.getFirst().getName());
    }

    @Test
    public void testShouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);
        manager.addEpic(epic);
        Subtask actualSubtask = manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask jsonSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(actualSubtask, jsonSubtask);
    }

    @Test
    public void testShouldRemoveSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");

        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getSubtasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void testGetSubtaskShouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtaskShouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testShouldNotAddSubtaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");

        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        Subtask subtaskUpdate = new Subtask("Subtask_update", "Subtask_update_description",
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(45), epic);

        manager.addEpic(epic);
        manager.addSubtask(subtask);
        String taskJson = gson.toJson(subtaskUpdate);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testShouldNotUpdateSubtaskIntersection() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45));
        Epic epic = new Epic("Epic", "Epic_description");

        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        Subtask subtaskUpdate = new Subtask(2, "Subtask_update", "Subtask_update_description",
                LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(45), epic);

        manager.addTask(task);
        manager.addTask(epic);
        manager.addSubtask(subtask);

        assertEquals(2, subtask.getId());

        String taskJson = gson.toJson(subtaskUpdate);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testShouldReturn400WithEmptyBody() throws IOException, InterruptedException {
        String subtaskJson = "";

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}

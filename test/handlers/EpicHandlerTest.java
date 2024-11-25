package handlers;

import adapters.DurationAdapter;
import adapters.EpicListTypeToken;
import adapters.LocalDateTimeAdapter;
import adapters.SubtaskListTypeToken;
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

class EpicHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public EpicHandlerTest() throws IOException {
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
    public void testShouldGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Epic epic2 = new Epic("Epic_2", "Epic_2_description", LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(45));
        manager.addEpic(epic);
        manager.addEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epics = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(epics);
        assertEquals(2, epics.size());
        assertEquals("Epic", epics.getFirst().getName());
        assertEquals("Epic_2", epics.get(1).getName());
    }

    @Test
    public void testShouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description", LocalDateTime.now(), Duration.ofMinutes(45));
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getEpics();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Epic", epics.getFirst().getName());
    }

    @Test
    public void testShouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description", LocalDateTime.now(), Duration.ofMinutes(45));
        manager.addEpic(epic);

        Epic updateEpic = new Epic(0, "Task_update", "Task_update_description", LocalDateTime.now().plusMinutes(40),
                Duration.ofMinutes(75));
        String epicJson = gson.toJson(updateEpic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epics = manager.getEpics();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals("Task_update", epics.getFirst().getName());
    }

    @Test
    public void testShouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description", LocalDateTime.now(), Duration.ofMinutes(45));
        Epic actualEpic = manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic jsonEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(actualEpic, jsonEpic);
    }

    @Test
    public void testShouldRemoveEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description", LocalDateTime.now(), Duration.ofMinutes(45));

        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getEpics().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void testShouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testReturn400WithEmptyBody() throws IOException, InterruptedException {
        String epicJson = "";

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testShouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic_description");
        Subtask subtask = new Subtask("Subtask", "Subtask_description", LocalDateTime.now(),
                Duration.ofMinutes(45), epic);

        manager.addEpic(epic);
        Subtask actualSubtask = manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(actualSubtask, subtasks.get(0));
    }
}
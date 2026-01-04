package tests;

import client.HttpTaskClient;
import com.google.gson.Gson;
import model.Task;
import model.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer.KVServer;
import service.HttpTaskServer;
import service.Managers;
import service.TaskManager;
import utils.GsonFactory;
import utils.TestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpServerTest<T extends Task> {
    private final Gson gson = GsonFactory.createGson();
    private TaskManager<Task> taskManager;
    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private HttpClient client;
    private HttpTaskClient<Task> myClient;

    @BeforeEach
    void SetUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        this.client = HttpClient.newHttpClient();
        this.myClient = new HttpTaskClient<>(client);
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        HttpResponse<String> response = myClient.createTask(Type.TASK, "task1", "task1", LocalDateTime.parse("01.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER), Duration.ofMinutes(15));
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = (List<T>) taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
    }

//    @Test
//    void getTask() throws IOException, InterruptedException {
//        TestUtils.createTask(taskManager, "task1", "task1"
//                , "01.11.2020 00:00", 15);
//
//        HttpResponse<String> response = myClient.getTaskById(1, Type.TASK);
//        Assertions.assertEquals(200, response.statusCode());
//    }


}

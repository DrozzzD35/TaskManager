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
    private KVServer kvServer;
    private TaskManager<T> taskManager;
    private HttpTaskServer<T> taskServer;
    private HttpTaskClient<T> myClient;

    @BeforeEach
    void SetUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer<>(taskManager);
        taskServer.start();

        HttpClient client = HttpClient.newHttpClient();
        this.myClient = new HttpTaskClient<>(client);
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        HttpResponse<String> response = myClient.createTask(Type.TASK, "task1", "task1"
                , LocalDateTime.parse("01.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER), Duration.ofMinutes(15));
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = myClient.createEpic(Type.EPIC, "task1", "task1");
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        myClient.createEpic(Type.EPIC, "task1", "task1");
        HttpResponse<String> response = myClient.createSubTask(Type.SUBTASK
                , "task2", "task2"
                , LocalDateTime.parse("01.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(15)
                , 1);
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(2, tasks.size(), "Список задач должен содержать 2 элемент");
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        addEpic();

        HttpResponse<String> response = myClient.getTaskById(1, Type.TASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        addTask();
        HttpResponse<String> response = myClient.removeTask(1, Type.TASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeEpic() throws IOException, InterruptedException {
        addEpic();
        HttpResponse<String> response = myClient.removeTask(1, Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeSubTask() throws IOException, InterruptedException {
        addSubTask();
        HttpResponse<String> response = myClient.removeTask(2, Type.SUBTASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeTasks() throws IOException, InterruptedException {
        TestUtils.createTask(taskManager, "task1", "task1"
                , "01.12.2020 00:00", 15);
        TestUtils.createTask(taskManager, "task1", "task1"
                , "01.12.2020 00:00", 15);
        TestUtils.createTask(taskManager, "task1", "task1"
                , "01.12.2020 00:00", 15);

        HttpResponse<String> response = myClient.removeTasks(Type.TASK);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size());
    }

    @Test
    void removeEpics() throws IOException, InterruptedException {
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");

        TestUtils.getSubTask(taskManager, "01.10.2020 00:00", 1);
        TestUtils.getSubTask(taskManager, "02.10.2020 00:00", 2);
        TestUtils.getSubTask(taskManager, "03.10.2020 00:00", 1);

        List<T> epics = taskManager.getTasks(Type.EPIC);
        Assertions.assertEquals(4, epics.size(), "Эпиков должно быть 4");
        List<T> subTask = taskManager.getTasks(Type.SUBTASK);
        Assertions.assertEquals(3, subTask.size(), "Эпиков должно быть 4");

        HttpResponse<String> response = myClient.removeTasks(Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, tasks.size(), "Список задач должен быть пуст");
    }

    @Test
    void removeSubTasks() throws IOException, InterruptedException {
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");

        TestUtils.getSubTask(taskManager, "01.10.2020 00:00", 1);
        TestUtils.getSubTask(taskManager, "02.10.2020 00:00", 1);
        TestUtils.getSubTask(taskManager, "03.10.2020 00:00", 2);





    }




}

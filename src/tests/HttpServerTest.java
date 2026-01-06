package tests;

import client.HttpTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
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
    private HttpTaskClient<T> taskClient;
    private Gson gson = GsonFactory.createGson();

    @BeforeEach
    void SetUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer<>(taskManager);
        taskServer.start();

        HttpClient client = HttpClient.newHttpClient();
        this.taskClient = new HttpTaskClient<>(client);
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.createTask(Type.TASK, "task1", "task1"
                , LocalDateTime.parse("01.10.2020 00:00"
                        , GsonFactory.DATE_TIME_FORMATTER), Duration.ofMinutes(15));
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.createEpic(Type.EPIC, "task1", "task1");
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "name", "name");
        SubTask subTask = TestUtils.createSubTask(taskManager, "02.10.2020 00:00", 1);
        HttpResponse<String> response = taskClient.createSubTask(Type.SUBTASK
                , "task2", "task2"
                , LocalDateTime.parse("01.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(15)
                , 1);
        Assertions.assertEquals(201, response.statusCode());

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicStatus(epic.getId());
        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(3, tasks.size(), "Список задач должен содержать 2 элемент");
        Assertions.assertEquals(List.of(2, 3), epic.getChildrenIds());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        addEpic();

        HttpResponse<String> response = taskClient.getTaskById(1, Type.TASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        addTask();
        HttpResponse<String> response = taskClient.removeTask(1, Type.TASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeEpic() throws IOException, InterruptedException {
        addEpic();
        HttpResponse<String> response = taskClient.removeTask(1, Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeSubTask() throws IOException, InterruptedException {
        addSubTask();
        HttpResponse<String> response = taskClient.removeTask(2, Type.SUBTASK);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void removeTasks() throws IOException, InterruptedException {
        TestUtils.createTask(taskManager, "task1", "task1"
                , "01.12.2020 00:00", 15);
        TestUtils.createTask(taskManager, "task1", "task1"
                , "02.12.2020 00:00", 15);
        TestUtils.createTask(taskManager, "task1", "task1"
                , "03.12.2020 00:00", 15);

        HttpResponse<String> response = taskClient.removeTasks(Type.TASK);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, tasks.size(), "Список задач должен быть пуст");
    }

    @Test
    void removeEpics() throws IOException, InterruptedException {
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");

        TestUtils.createSubTask(taskManager, "01.10.2020 00:00", 1);
        TestUtils.createSubTask(taskManager, "02.10.2020 00:00", 2);
        TestUtils.createSubTask(taskManager, "03.10.2020 00:00", 1);

        List<T> epics = taskManager.getTasks(Type.EPIC);
        Assertions.assertEquals(4, epics.size(), "Эпиков должно быть 4");
        List<T> subTask = taskManager.getTasks(Type.SUBTASK);
        Assertions.assertEquals(3, subTask.size(), "Эпиков должно быть 4");

        HttpResponse<String> response = taskClient.removeTasks(Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, tasks.size(), "Список задач должен быть пуст");
    }

    @Test
    void removeSubTasks() throws IOException, InterruptedException {
        Epic epic1 = TestUtils.createEpic(taskManager, "task1", "task1");
        Epic epic2 = TestUtils.createEpic(taskManager, "task1", "task1");

        SubTask subTask = TestUtils.createSubTask(taskManager, "01.10.2020 00:00", 1);
        TestUtils.createSubTask(taskManager, "02.10.2020 00:00", 1);
        TestUtils.createSubTask(taskManager, "03.10.2020 00:00", 2);

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicStatus(1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());

        HttpResponse<String> response = taskClient.removeTasks(Type.SUBTASK);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(2, tasks.size());

        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus());
        Assertions.assertEquals(TaskStatus.NEW, epic2.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    void updateTask() throws IOException, InterruptedException {
        Task task = TestUtils.createTask(taskManager, "02.10.2020 00:00");
        Task newTask = new Task("updateName", "updateDescription"
                , LocalDateTime.parse("03.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(15));

        HttpResponse<String> response = taskClient.updateTask(task.getId(), (T) newTask);
        Task updatedTask = taskManager.getTaskById(task.getId(), false);
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("updateName", updatedTask.getName());
        Assertions.assertEquals("updateDescription", updatedTask.getDescription());
        Assertions.assertEquals(15, updatedTask.getDuration().toMinutes());
        Assertions.assertEquals("03.10.2020 00:00", updatedTask.getStartTime().format(GsonFactory.DATE_TIME_FORMATTER));
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = TestUtils.createTask(taskManager, "03.10.2020 00:00");
        taskClient.getTaskById(task.getId(), Type.TASK);
        HttpResponse<String> response = taskClient.getHistory();
        Assertions.assertEquals(200, response.statusCode());

        List<T> historyFromServer = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        Assertions.assertEquals(1, historyFromServer.size(), "Список должен содержать 1 задачу");
        Assertions.assertEquals(task.getName(), historyFromServer.getFirst().getName() );
        Assertions.assertEquals(task.getId(), historyFromServer.getFirst().getId() );
    }

    @SuppressWarnings("unchecked")
    @Test
    void removeHistory() throws IOException, InterruptedException {
        getHistory();
        HttpResponse<String> response = taskClient.removeHistory();
        Assertions.assertEquals(200, response.statusCode());

        List<T> history = taskManager.getHistory().getHistory();
        Assertions.assertEquals(0, history.size(), "Список истории должен быть пуст");

        Task task = TestUtils.createTask(taskManager, "05.10.2020 00:00");
        Task task2 = TestUtils.createTask(taskManager, "04.10.2020 00:00");
        taskManager.getTaskById(task.getId(), true);
        taskManager.getTaskById(task2.getId(), true);
        taskManager.getHistory().removeTask((T) task2);
        List<T> history2 = taskManager.getHistory().getHistory();
        Assertions.assertEquals(1, history2.size(), "История должна содержать 1 задачу");
    }

}

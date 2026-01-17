package tests;

import client.HttpTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import org.apiguardian.api.API;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer.KVServer;
import service.HistoryManager;
import service.HttpTaskServer;
import service.Managers;
import service.TaskManager;
import utils.GsonFactory;
import utils.Identity;
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
    private final Gson gson = GsonFactory.createGson();
    private HistoryManager<T> historyManager;

    @BeforeEach
    void SetUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer<>(taskManager);
        taskServer.start();

        HttpClient client = HttpClient.newHttpClient();
        this.taskClient = new HttpTaskClient<>(client);
        this.historyManager = taskManager.getHistory();
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
        taskManager.removeAllTasks();
        historyManager.removeHistory();
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = taskClient.createEpic(Type.EPIC, "task1", "task1");
        Assertions.assertEquals(201, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, tasks.size(), "Список задач должен содержать 1 элемент");
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "name", "name");

        HttpResponse<String> response = taskClient.createSubTask(Type.SUBTASK
                , "task2", "task2"
                , LocalDateTime.parse("01.10.2020 00:00", GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(15)
                , epic.getId());
        Assertions.assertEquals(201, response.statusCode());
        SubTask subTaskFromResponse = gson.fromJson(response.body(), SubTask.class);

        SubTask subTask = TestUtils.createSubTask(taskManager, "02.10.2020 00:00", epic.getId());
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicStatus(epic.getId());
        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(3, tasks.size(), "Список задач должен содержать 2 элемент");
        Assertions.assertEquals(List.of(subTaskFromResponse.getId(), subTask.getId()), epic.getChildrenIds());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void getTask() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "epic", "epic");

        HttpResponse<String> response1 = taskClient.getTaskById(epic.getId(), Type.EPIC);
        Assertions.assertEquals(200, response1.statusCode());

        HttpResponse<String> response3 = taskClient.getTaskById(200, Type.TASK);
        Assertions.assertEquals(404, response3.statusCode());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void removeTask() throws IOException, InterruptedException {
        Task task = TestUtils.createTask(taskManager, "03.10.2020 00:00");
        HttpResponse<String> response = taskClient.removeTask(task.getId(), Type.TASK);
        Assertions.assertEquals(200, response.statusCode());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void removeEpic() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "epic", "epic");
        HttpResponse<String> response = taskClient.removeTask(epic.getId(), Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void removeSubTask() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "epic", "epic");
        SubTask subTask = TestUtils.createSubTask(taskManager, "04.10.2020 00:00", epic.getId());

        HttpResponse<String> response = taskClient.removeTask(subTask.getId(), Type.SUBTASK);
        Assertions.assertEquals(200, response.statusCode());

        HttpResponse<String> response2 = taskClient.getTaskById(epic.getId(), Type.EPIC);
        Assertions.assertEquals(200, response2.statusCode());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

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
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void removeEpics() throws IOException, InterruptedException {
        Epic epic1 = TestUtils.createEpic(taskManager, "task1", "task1");
        Epic epic2 = TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");
        TestUtils.createEpic(taskManager, "task1", "task1");

        TestUtils.createSubTask(taskManager, "01.10.2020 00:00", epic1.getId());
        TestUtils.createSubTask(taskManager, "02.10.2020 00:00", epic1.getId());
        TestUtils.createSubTask(taskManager, "03.10.2020 00:00", epic2.getId());

        List<T> epics = taskManager.getTasks(Type.EPIC);
        Assertions.assertEquals(4, epics.size(), "Эпиков должно быть 4");
        List<T> subTask = taskManager.getTasks(Type.SUBTASK);
        Assertions.assertEquals(3, subTask.size(), "Эпиков должно быть 4");

        HttpResponse<String> response = taskClient.removeTasks(Type.EPIC);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(0, tasks.size(), "Список задач должен быть пуст");
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void removeSubTasks() throws IOException, InterruptedException {
        Epic epic1 = TestUtils.createEpic(taskManager, "task1", "task1");
        Epic epic2 = TestUtils.createEpic(taskManager, "task1", "task1");

        SubTask subTask = TestUtils.createSubTask(taskManager, "01.10.2020 00:00", epic1.getId());
        TestUtils.createSubTask(taskManager, "02.10.2020 00:00", 1);
        TestUtils.createSubTask(taskManager, "03.10.2020 00:00", 2);

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicStatus(epic1.getId());
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());

        HttpResponse<String> response = taskClient.removeTasks(Type.SUBTASK);
        Assertions.assertEquals(200, response.statusCode());

        List<T> tasks = taskManager.getAllTasks();
        Assertions.assertEquals(2, tasks.size());

        Assertions.assertEquals(TaskStatus.NEW, epic1.getStatus());
        Assertions.assertEquals(TaskStatus.NEW, epic2.getStatus());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

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
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = TestUtils.createTask(taskManager, "03.10.2020 00:00");
        taskClient.getTaskById(task.getId(), Type.TASK);
        HttpResponse<String> response = taskClient.getHistory();
        Assertions.assertEquals(200, response.statusCode());

        List<T> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "Список должен содержать 1 задачу");
        Assertions.assertEquals(task.getName(), history.getFirst().getName());
        Assertions.assertEquals(task.getId(), history.getFirst().getId());
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @SuppressWarnings("unchecked")
    @Test
    void removeHistory() throws IOException, InterruptedException {
        Task task = TestUtils.createTask(taskManager,"06.10.2020 00:00" );
        taskManager.getTaskById(task.getId(), true);
        List<T> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "История должна содержать 1 задачу");

        HttpResponse<String> response = taskClient.removeHistory();
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, history.size(), "Список истории должен быть пуст");

        Task task1 = TestUtils.createTask(taskManager, "05.10.2020 00:00");
        Task task2 = TestUtils.createTask(taskManager, "04.10.2020 00:00");
        taskManager.getTaskById(task1.getId(), true);
        taskManager.getTaskById(task2.getId(), true);
        taskManager.getHistory().removeTask((T) task2);
        List<T> history2 = taskManager.getHistory().getHistory();
        Assertions.assertEquals(1, history2.size(), "История должна содержать 1 задачу");
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }

    @SuppressWarnings("unchecked")
    @Test
    void updateEpicStatus() throws IOException, InterruptedException {
        Epic epic = TestUtils.createEpic(taskManager, "Epic", "Description");
        SubTask subTask = TestUtils.createSubTask(taskManager, "04.10.2020 00:00", 1);
        subTask.setStatus(TaskStatus.IN_PROGRESS);

        HttpResponse<String> response = taskClient.updateTask(subTask.getId(), (T) subTask);
        Assertions.assertEquals(200, response.statusCode());
        TaskStatus status = epic.getStatus();
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, status, "Статус Эпика должен быть IN_PROGRESS");
        taskManager.removeAllTasks();
        historyManager.removeHistory();

    }
}

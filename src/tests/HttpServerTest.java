package tests;

import com.google.gson.Gson;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer.KVServer;
import service.HttpTaskServer;
import service.Managers;
import service.TaskManager;
import utils.GsonFactory;
import utils.TestUtils;

import java.io.IOException;

public class HttpServerTest {
    private final Gson gson = GsonFactory.createGson();
    private TaskManager<Task> taskManager;
    private HttpTaskServer taskServer;
    private KVServer kvServer;

    @BeforeEach
    void SetUp() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void addTask() {
        TestUtils.createTask
                (taskManager, "task1", "task1", "01.10.2020 00:00", 40);
    }


}

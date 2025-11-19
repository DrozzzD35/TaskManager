package server;

import com.sun.net.httpserver.HttpServer;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class HttpTaskServer {
    private TaskManager<Task> taskManager;
    private final HttpServer server;

    public static void main(String[] args) throws IOException {
        Path path = Path.of("src/dataBacked/FileBacked.CSV");
        FileBackedTasksManager<Task> fileBackedTasksManager =
                (FileBackedTasksManager<Task>) Managers.getDefaultFile(path);
        HttpTaskServer taskServer = new HttpTaskServer(fileBackedTasksManager);
        taskServer.start();
    }

    public HttpTaskServer(TaskManager<Task> taskManager) throws IOException {
        int port = 8080;
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tasks/task/", new TaskHandler<>(taskManager));
        server.createContext("/tasks/epic/", new EpicHandler<>(taskManager));
        server.createContext("/tasks/subTask/", new SubTaskHandler<>(taskManager));
        server.createContext("/tasks/history", new HistoryHandler<>(taskManager));
        server.createContext("/tasks/", new GeneralTaskHandler<>(taskManager));

        start();
    }

    public void start() {
        System.out.println("Запускаем сервер. Адрес http://localhost:8080/");
        server.start();
    }


}

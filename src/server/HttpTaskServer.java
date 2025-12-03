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
    private final HttpServer server;

    public HttpTaskServer(TaskManager<Task> taskManager) throws IOException {
        int port = 8080;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tasks/task/", new TaskHandler<>(taskManager));
        server.createContext("/tasks/epic/", new EpicHandler<>(taskManager));
        server.createContext("/tasks/subTask/", new SubTaskHandler<>(taskManager));
        server.createContext("/tasks/history", new HistoryHandler<>(taskManager));
        server.createContext("/tasks/", new TasksHandler<>(taskManager));

    }

    public void start() {
        System.out.println("Запускаем сервер. Адрес http://localhost:8080/");
        server.start();
    }


}

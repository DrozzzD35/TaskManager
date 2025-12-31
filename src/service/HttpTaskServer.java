package service;

import com.sun.net.httpserver.HttpServer;
import model.Task;
import server.Config;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        TaskManager<Task> taskManager = Managers.getDefault();
        Config config = new Config();
        this.server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

        server.createContext(config.getTasks() + config.getTask(), new TaskHandler<>(taskManager));
        server.createContext(config.getTasks() + config.getEpic(), new EpicHandler<>(taskManager));
        server.createContext(config.getTasks() + config.getSubTask(), new SubTaskHandler<>(taskManager));
        server.createContext(config.getTasks() + config.getHistory(), new HistoryHandler<>(taskManager));
        server.createContext(config.getTasks(), new TasksHandler<>(taskManager));
    }

    public void start() {
        System.out.println("Запускаем сервер. Адрес http://localhost:8080/");
        server.start();
    }


}

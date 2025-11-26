package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class GeneralTaskHandler<T extends Task> implements HttpHandler {
    private TaskManager<T> taskManager;

    public GeneralTaskHandler(TaskManager<T> tasksManager) {
        this.taskManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {


    }
}

package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler<T extends Task> implements HttpHandler {
    private TaskManager<T> taskManager;

    public HistoryHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}

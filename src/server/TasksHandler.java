package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class TasksHandler<T extends Task> extends BaseHandler<T> {

    public TasksHandler(TaskManager<T> tasksManager) {
        super(tasksManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode;

        try {

            if (method.equals("GET")) {
                response = gson.toJson(taskManager.getPrioritizedTasks());
                statusCode = 200;
            } else if (method.equals("DELETE")) {
                taskManager.removeAllTasks();
                response = gson.toJson("Все задачи удалены");
                statusCode = 200;
            } else {
                response = gson.toJson("Неизвестная команда");
                statusCode = 501;
            }
        } catch (Exception e) {
            response = gson.toJson("Неизвестная ошибка");
            statusCode = 500;
        }


        sendResponse(exchange, statusCode, response);

    }

}

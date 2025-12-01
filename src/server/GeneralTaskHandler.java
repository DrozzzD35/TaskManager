package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;

public class GeneralTaskHandler<T extends Task> implements HttpHandler {
    private final TaskManager<T> taskManager;
    private final Gson gson = new Gson();

    public GeneralTaskHandler(TaskManager<T> tasksManager) {
        this.taskManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode;

        try {


            if (method.equals("GET")) {
                response = gson.toJson(taskManager.getAllTasks());
                statusCode = 200;
            } else if (method.equals("DELETE")) {
                taskManager.removeAllTasks();
                response = gson.toJson("Все задачи удалены");
                statusCode = 200;
            } else {
                response = gson.toJson("Неизвестная команда");
                statusCode = 501;
            }
        } catch (Exception e){
            response = gson.toJson("Неизвестная ошибка");
            statusCode = 500;
        }


        SubTaskHandler.sendResponse(exchange, statusCode, response);

    }

}

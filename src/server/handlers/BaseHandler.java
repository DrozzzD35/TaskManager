package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;
import utils.GsonFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHandler<T extends Task> implements HttpHandler {
    protected final TaskManager<T> taskManager;
    protected final Gson gson = GsonFactory.createGson();

    public BaseHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders()
                .add("Content-type", "application/json; Charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке данных");
        }
    }

    protected static int parseIdFromQuery(String queryString) {
        String[] string = queryString.split("=");
        return Integer.parseInt(string[1]);
    }

    protected T getTask(int id) {
        T task = taskManager.getTaskById(id, true);
        if (task == null) {
            System.out.println("Задачи не существует");
        }
        return task;
    }


}

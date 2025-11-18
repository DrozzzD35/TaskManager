package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import javax.management.Query;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class TaskHandler<T extends Task> implements HttpHandler {
    private final TaskManager<T> taskManager;
    private static final Gson gson = new Gson();


    public TaskHandler(TaskManager<T> tasksManager) {
        this.taskManager = tasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        String queryString = exchange.getRequestURI().getQuery();

        int statusCode = 0;
        String response = "";

        switch (method) {
            case "GET" -> {
                try {
                    if (queryString != null) {
                        String[] string = queryString.split("=");
                        int id = Integer.parseInt(string[string.length - 1]);
                        Task task = taskManager.getTaskById(id, false);

                        if (task != null) {
                            response = gson.toJson(task);
                            statusCode = 200;
                        } else {
                            response = gson.toJson("Задача не найдена");
                            statusCode = 404;
                        }

                    } else {
                        List<T> tasks = taskManager.getTasks();

                        if (!tasks.isEmpty()) {
                            response = gson.toJson(tasks);
                            statusCode = 200;
                        } else {
                            response = gson.toJson("Список задач пуст");
                            statusCode = 404;
                        }
                    }

                } catch (NumberFormatException e) {
                    response = gson.toJson(e.getStackTrace());
                    statusCode = 404;
                }

            }
            case "POST" -> {
                InputStream is = exchange.getRequestBody();
                String jsonString = new String(is.readAllBytes(), StandardCharsets.UTF_8);


            }
            case "DELETE" -> {

            }
            default -> {
                response = gson.toJson("Не удалось распознать запрос");
                statusCode = 501;

            }
        }

        exchange.getResponseHeaders().add("Content-type", "application/json; Charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        } catch (IOException e) {
            System.out.println("Не удалось отправить ответ " + e.getMessage());
        }

    }
}

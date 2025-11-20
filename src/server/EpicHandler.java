package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler<T extends Task> implements HttpHandler {
    private TaskManager<T> taskManager;
    private Gson gson = new Gson();

    public EpicHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        int statusCode = 0;
        String response = "";
        String stringQuery = exchange.getRequestURI().getQuery();

        switch (method) {
            case "GET" -> {
                try {
                    if (stringQuery != null) {
                        String[] parts = stringQuery.split("=");
                        int id = Integer.parseInt(parts[parts.length - 1]);

                        response = gson.toJson(taskManager.getTaskById(id, false));
                        statusCode = 200;
                    } else {
                        response = gson.toJson("Идентификатор не распознан");
                        statusCode = 400;
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    response = gson.toJson("Ошибка чтения URL");
                    statusCode = 400;
                }

            }
            case "POST" -> {
                InputStream os = exchange.getRequestBody();
                String stringJson = new String(os.readAllBytes(), StandardCharsets.UTF_8);

                try {
                    Epic epic = gson.fromJson(stringJson, Epic.class);

                    if (epic.getId() != 0 | epic.getId() != null) {
                        taskManager.updateTask((T) epic, epic.getId());
                        T updateEpic = taskManager.getTaskById(epic.getId(), false);
                        response = gson.toJson(updateEpic);
                        statusCode = 200;
                    } else {
                        taskManager.add((T) epic);
                        response = gson.toJson(epic);
                        statusCode = 201;
                    }

                } catch (ClassCastException e) {
                    response = gson.toJson("Ошибка привидения типа. Ожидаемый типа Task");
                    statusCode = 400;
                } catch (IllegalArgumentException | JsonSyntaxException e){
                    response = gson.toJson("Неверно указаны данные " + e.getMessage());
                    statusCode = 400;
                }

            }
            case "DELETE" -> {
                String[] part = stringQuery.split("=");
                int id = Integer.parseInt(part[part.length-1]);

                taskManager.removeTaskById(id);

            }
            default -> {

            }
        }

        exchange.getResponseHeaders().add("Content-type", "application/json; Charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке данных");
        }

    }
}

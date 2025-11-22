package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
        String queryString = exchange.getRequestURI().getQuery();

        int statusCode = 0;
        String response = "";

        switch (method) {
            case "GET" -> {
                try {
                    if (queryString != null) {
                        String[] string = queryString.split("=");
                        int id = Integer.parseInt(string[1]);
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

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    response = gson.toJson("Не распознан идентификатор. Либо ошибка в чтении URL");
                    statusCode = 400;
                }

            }
            case "POST" -> {
                InputStream is = exchange.getRequestBody();
                String stringJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                try {
                    Task taskJson = gson.fromJson(stringJson, Task.class);

                    if (taskJson.getId() != null && taskJson.getId() != 0) {
                        taskManager.updateTask((T) taskJson, taskJson.getId());
                        Task updateTask = taskManager.getTaskById(taskJson.getId(), false);

                        response = gson.toJson(updateTask);
                        statusCode = 200;
                    } else {
                        Task task = new Task(taskJson.getName(), taskJson.getDescription());
                        taskManager.add((T) task);
                        response = gson.toJson(task);
                        statusCode = 201;
                    }

                } catch (JsonSyntaxException | IllegalArgumentException e) {
                    response = gson.toJson("Неверно указаны данные " + e.getMessage());
                    statusCode = 400;

                } catch (ClassCastException e) {
                    response = gson.toJson("Ошибка привидения типа. Ожидаемый типа Task");
                    statusCode = 400;

                }

            }
            case "DELETE" -> {
                try {
                    if (queryString != null) {
                        String[] strings = queryString.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Task task = taskManager.getTaskById(id, false);

                        if (task != null) {
                            taskManager.removeTaskById(id);
                            response = gson.toJson("Задача с идентификатором " + id + " удалена.");
                            statusCode = 200;
                        }

                    } else {
                        taskManager.removeAllTasks();
                        response = gson.toJson("Задачи удалены ");
                        statusCode = 200;
                    }

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    response = gson.toJson("Не удалось распознать идентификатор " + e.getMessage());
                    statusCode = 400;
                }

            }
            default -> {
                response = gson.toJson("Не удалось распознать запрос");
                statusCode = 501;

            }
        }

        exchange.getResponseHeaders().
                add("Content-type", "application/json; Charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        } catch (IOException e) {
            System.out.println("Не удалось отправить ответ " + e.getMessage());
        }

    }
}

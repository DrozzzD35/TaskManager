package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler<T extends Task> implements HttpHandler {
    private TaskManager<T> taskManager;
    Gson gson = new Gson();

    public SubTaskHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String stringQuery = exchange.getRequestURI().getQuery();
        int statusCode = -1;
        String response = "";

        switch (method) {
            case "GET" -> {
                try {
                    if (stringQuery != null) {
                        String[] partQuery = stringQuery.split("=");
                        int id = Integer.parseInt(partQuery[1]);
                        T subTask = taskManager.getTaskById(id, false);

                        if (subTask == null) {
                            response = gson.toJson("Задачи с идентификатором "
                                    + id + " не существует");
                            statusCode = 404;
                            break;
                        }

                        if (subTask instanceof SubTask) {
                            response = gson.toJson(subTask);
                            statusCode = 200;
                        } else {
                            response = gson.toJson("Полученная задача не является SubTask");
                            statusCode = 404;
                        }
                    } else {
                        response = gson.toJson(taskManager.getSubTasks());
                        statusCode = 200;
                    }

                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    response = gson.toJson("Ошибка чтения URL " + e.getMessage());
                    statusCode = 400;
                }

            }
            case "POST" -> {
                InputStream os = exchange.getRequestBody();
                String stringJson = new String(os.readAllBytes(), StandardCharsets.UTF_8);

                try {
                    SubTask subTaskJson = gson.fromJson(stringJson, SubTask.class);

                    if (subTaskJson.getId() != null && subTaskJson.getId() != 0) {
                        taskManager.updateTask((T) subTaskJson, subTaskJson.getId());
                        response = gson.toJson(taskManager.getTaskById(subTaskJson.getId(), false));
                        statusCode = 200;
                    } else {
                        SubTask subTask = new SubTask(subTaskJson.getName(), subTaskJson.getDescription(), subTaskJson.getParentId());
                        taskManager.add((T) subTask);
                        response = gson.toJson(subTask);
                        statusCode = 201;
                    }

                } catch (JsonSyntaxException | IllegalArgumentException e) {
                    response = gson.toJson("Ошибка чтения данных");
                    statusCode = 400;

                } catch (ClassCastException e) {
                    response = gson.toJson("Ошибка привидения типа. Ожидается тип SubTask " + e.getMessage());
                    statusCode = 400;
                }
            }

            case "DELETE" -> {

                try {
                    if (stringQuery != null) {
                        String[] part = stringQuery.split("=");
                        int id = Integer.parseInt(part[1]);
                        T task = taskManager.getTaskById(id, false);

                        if (task == null) {
                            response = gson.toJson("Задачи с идентификатором "
                                    + id + " не существует");
                            statusCode = 404;
                            break;
                        }

                        if (task instanceof SubTask) {
                            taskManager.removeTaskById(id);
                            response = gson.toJson("Задача с идентификатором "
                                    + id + " удалена.");
                            statusCode = 200;
                        } else {
                            response = gson.toJson("Найденная задача не является SubTask. Ожидается SubTask");
                            statusCode = 404;
                        }
                    } else {
                        taskManager.removeSubTasks();
                        response = gson.toJson("Все SubTasks удалены");
                        statusCode = 200;
                    }

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    response = gson.toJson("Ошибка чтения URL");
                    statusCode = 404;
                }

            }

            default -> {
                response = gson.toJson("Запрос не может быть обработан. Допущена ошибка");
                statusCode = 501;
            }

        }
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
}

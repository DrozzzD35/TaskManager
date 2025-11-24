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
import java.nio.charset.StandardCharsets;
//TODO стоит ли проверять по типу прежде чем удалять\отправлять

public class EpicHandler<T extends Task> implements HttpHandler {
    private TaskManager<T> taskManager;
    private Gson gson = new Gson();

    public EpicHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        int statusCode;
        String response;
        String stringQuery = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET" -> {
                    if (stringQuery != null) {
                        int id = parseIdFromQuery(stringQuery);
                        T epic = getTask(id);
                        validateEpicType(epic);
                        response = gson.toJson(epic);
                        statusCode = 200;
                    } else {
                        response = gson.toJson(taskManager.getEpics());
                        statusCode = 200;
                    }
                }
                case "POST" -> {
                    InputStream os = exchange.getRequestBody();
                    String stringJson = new String(os.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epicJson = gson.fromJson(stringJson, Epic.class);

                    if (epicJson.getId() != null && epicJson.getId() != 0) {
                        taskManager.updateTask((T) epicJson, epicJson.getId());
                        T updateEpic = taskManager.getTaskById(epicJson.getId(), false);
                        response = gson.toJson(updateEpic);
                        statusCode = 200;

                    } else {
                        Epic epic = new Epic(epicJson.getName(), epicJson.getDescription());
                        taskManager.add((T) epic);
                        response = gson.toJson(epic);
                        statusCode = 201;
                    }
                }
                case "DELETE" -> {
                    if (stringQuery != null) {
                        int id = parseIdFromQuery(stringQuery);
                        T task = getTask(id);
                        validateEpicType(task);
                        taskManager.removeTaskById(id);
                        response = gson.toJson("Задача с идентификатором " + id + " удалена.");
                        statusCode = 201;
                    } else {
                        taskManager.removeEpics();
                        response = gson.toJson("Все задачи типа Epic были удалены");
                        statusCode = 400;
                    }
                }
                default -> {
                    response = gson.toJson("Не удалось распознать запрос");
                    statusCode = 501;
                }
            }

        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            response = gson.toJson("Ошибка чтения URL");
            statusCode = 400;

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            response = gson.toJson("Неверно указаны данные " + e.getMessage());
            statusCode = 400;
        }

        SubTaskHandler.sendResponse(exchange, statusCode, response);

    }

    private static int parseIdFromQuery(String stringQuery) {
        String[] part = stringQuery.split("=");
        int id = Integer.parseInt(part[1]);
        return id;
    }

    private void validateEpicType(T task) {
        if (task instanceof Epic) {
            throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Epic");
        }
    }

    private T getTask(int id) {
        T task = taskManager.getTaskById(id, false);
        if (task == null) {
            throw new NotFoundException(id);
        }
        return task;
    }

}

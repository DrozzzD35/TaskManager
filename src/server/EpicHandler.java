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

        switch (method) {
            case "GET" -> {
                try {
                    if (stringQuery != null) {
                        String[] parts = stringQuery.split("=");
                        int id = Integer.parseInt(parts[1]);
                        T epic = taskManager.getTaskById(id, false);

                        if (epic == null) {
                            response = gson.toJson("Задачи с идентификатором "
                                    + id + " не существует");
                            statusCode = 404;
                            break;
                        }

                        if (epic instanceof Epic) {
                            response = gson.toJson(epic);
                            statusCode = 200;
                        } else {
                            response = gson.toJson("Задача не имеет тип Epic. Требуется тип Epic");
                            statusCode = 404;
                        }
                    } else {
                        response = gson.toJson(taskManager.getEpics());
                        statusCode = 200;
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

                } catch (ClassCastException e) {
                    response = gson.toJson("Ошибка привидения типа. Ожидаемый типа Task");
                    statusCode = 400;
                } catch (IllegalArgumentException | JsonSyntaxException e) {
                    response = gson.toJson("Неверно указаны данные " + e.getMessage());
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

                        if (task instanceof Epic) {
                            taskManager.removeTaskById(id);
                            response = gson.toJson("Задача с идентификатором " + id + " удалена.");
                            statusCode = 201;
                        } else {
                            response = gson.toJson("Неверный тип задачи. Ожидается Epic");
                            statusCode = 404;
                        }
                    } else {
                        taskManager.removeEpics();
                        response = gson.toJson("Все задачи типа Epic были удалены");
                        statusCode = 400;
                    }

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    response = gson.toJson("Ошибка чтения URL");
                    statusCode = 400;
                }
            }
            default -> {
                response = gson.toJson("Не удалось распознать запрос");
                statusCode = 501;
            }
        }

        SubTaskHandler.sendResponse(exchange, statusCode, response);

    }
}

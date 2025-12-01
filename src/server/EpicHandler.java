package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Task;
import model.Type;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
//TODO стоит ли проверять по типу прежде чем удалять\отправлять

public class EpicHandler<T extends Task> implements HttpHandler {
    private final TaskManager<T> taskManager;
    private final Gson gson = new Gson();

    public EpicHandler(TaskManager<T> taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String stringQuery = exchange.getRequestURI().getQuery();
        int statusCode;
        String response;

        try {
            switch (method) {
                case "GET" -> {
                    if (stringQuery != null) {
                        int id = parseIdFromQuery(stringQuery);
                        T epic = getTask(id);
                        validateEpicType(epic);
                        response = gson.toJson(epic);
                    } else {
                        chekListOfEpics();
                        response = gson.toJson(taskManager.getTasks(Type.EPIC));
                    }
                    statusCode = 200;
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
                    } else {
                        taskManager.removeTasks(Type.EPIC);
                        response = gson.toJson("Все задачи типа Epic были удалены");
                    }
                    statusCode = 200;

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
        } catch (InCorrectClassException e) {
            response = gson.toJson(e.getMessage());
            statusCode = 400;
        } catch (NotFoundException e) {
            response = gson.toJson(e.getMessage());
            statusCode = 404;
        }

        SubTaskHandler.sendResponse(exchange, statusCode, response);

    }

    private static int parseIdFromQuery(String stringQuery) {
        String[] part = stringQuery.split("=");
        return Integer.parseInt(part[1]);
    }

    private void validateEpicType(T task) {
        if (!(task instanceof Epic)) {
            throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Epic");
        }
    }

    private T getTask(int id) {
        T task = taskManager.getTaskById(id, false);
        if (task == null) {
            throw new NotFoundException("Задача не существует");
        }
        return task;
    }

    private void chekListOfEpics() {
        if (taskManager.getTasks(Type.EPIC).isEmpty()) {
            throw new NotFoundException("Список Epics пуст");
        }
    }


}

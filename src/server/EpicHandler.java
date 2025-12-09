package server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Task;
import model.Type;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
//TODO PUT - обновление

public class EpicHandler<T extends Task> extends BaseHandler<T> {

    public EpicHandler(TaskManager<T> taskManager) {
        super(taskManager);
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
                case "PUT" -> {
                    InputStream is = exchange.getRequestBody();
                    String taskString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Epic json = gson.fromJson(taskString, Epic.class);
                    int id = parseIdFromQuery(stringQuery);
                    T oldTask = taskManager.getTaskById(id, false);
                    validateEpicType(oldTask);
                    taskManager.updateTask((T) json, id);
                    //TODO Возник спор с ИИ нужно ли заново искать задачу или можно отправить gson.toJson(oldTask)
                    T updatedTask = taskManager.getTaskById(id,false);

                    response = gson.toJson(updatedTask);
                    statusCode = 200;

                }
                case "POST" -> {
                    InputStream os = exchange.getRequestBody();
                    String stringJson = new String(os.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epicJson = gson.fromJson(stringJson, Epic.class);
                    Epic epic = new Epic(epicJson.getName(), epicJson.getDescription());
                    taskManager.add((T) epic);

                    response = gson.toJson(epic);
                    statusCode = 201;

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

        sendResponse(exchange, statusCode, response);

    }

    private void validateEpicType(T task) {
        if (!(task instanceof Epic)) {
            throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Epic");
        }
    }

    private void chekListOfEpics() {
        if (taskManager.getTasks(Type.EPIC).isEmpty()) {
            throw new NotFoundException("Список Epics пуст");
        }
    }


}

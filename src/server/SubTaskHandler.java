package server;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import model.Task;
import model.Type;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler<T extends Task> extends BaseHandler<T> {

    public SubTaskHandler(TaskManager<T> taskManager) {
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
                        T task = getTask(id);
                        validateSubTaskType(task);
                        response = gson.toJson(task);

                    } else {
                        chekListOfSubTasks();
                        response = gson.toJson(taskManager.getTasks(Type.SUBTASK));
                    }
                    statusCode = 200;

                }
                case "POST" -> {
                    InputStream os = exchange.getRequestBody();
                    String stringJson = new String(os.readAllBytes(), StandardCharsets.UTF_8);
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

                }
                case "DELETE" -> {
                    if (stringQuery != null) {
                        int id = parseIdFromQuery(stringQuery);
                        T task = getTask(id);
                        validateSubTaskType(task);
                        taskManager.removeTaskById(id);
                        response = gson.toJson("Задача с идентификатором "
                                + id + " удалена.");
                    } else {
                        taskManager.removeTasks(Type.SUBTASK);
                        response = gson.toJson("Все SubTasks удалены");
                    }
                    statusCode = 200;

                }
                default -> {
                    response = gson.toJson("Запрос не может быть обработан. Допущена ошибка");
                    statusCode = 501;
                }
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            response = gson.toJson("Ошибка чтения URL");
            statusCode = 404;

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            response = gson.toJson("Ошибка чтения данных");
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

    private void validateSubTaskType(T task) {
        if (!(task instanceof SubTask)) {
            throw new InCorrectClassException("Тип задачи не корректный. Ожидаемый тип SubTask");
        }
    }

    private void chekListOfSubTasks(){
        if (taskManager.getTasks(Type.SUBTASK).isEmpty()){
            throw new NotFoundException("Список SubTasks пуст");
        }
    }

}

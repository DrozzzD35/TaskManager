package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import model.Type;
import server.exception.InCorrectClassException;
import server.exception.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler<T extends Task> extends BaseHandler<T> {

    public TaskHandler(TaskManager<T> tasksManager) {
        super(tasksManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String queryString = exchange.getRequestURI().getQuery();
        int statusCode;
        String response;

        try {
            switch (method) {
                case "GET" -> {
                    if (queryString != null) {
                        int id = parseIdFromQuery(queryString);
                        Task task = getTask(id);
                        response = gson.toJson(task);
                    } else {
                        chekListOfTasks();
                        response = gson.toJson(taskManager.getTasks(Type.TASK));
                    }
                    statusCode = 200;
                }
                case "PUT" -> {
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Task json = gson.fromJson(body, Task.class);

                    int id = parseIdFromQuery(queryString);
                    T oldTask = taskManager.getTaskById(id, false);
                    validateTaskType(oldTask);
                    taskManager.updateTask((T) json, id);
                    T updatedTask = taskManager.getTaskById(id, false);

                    response = gson.toJson(updatedTask);
                    statusCode = 200;

                }
                case "POST" -> {
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Task json = gson.fromJson(body, Task.class);
                    Task task = new Task(json.getName(), json.getDescription()
                            , json.getStartTime(), json.getDuration());
                    taskManager.add((T) task);

                    response = gson.toJson(task);
                    statusCode = 201;

                }
                case "DELETE" -> {
                    if (queryString != null) {
                        int id = parseIdFromQuery(queryString);
                        T task = getTask(id);
                        validateTaskType(task);

                        taskManager.removeTaskById(id);
                        response = gson.toJson("Задача с идентификатором " + id + " удалена.");

                    } else {
                        taskManager.removeTasks(Type.TASK);
                        response = gson.toJson("Задачи удалены ");
                    }
                    statusCode = 200;
                }
                default -> {
                    response = gson.toJson("Не удалось распознать запрос");
                    statusCode = 501;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка " + e.getClass().getName() + " "
                    + e.getMessage());
            e.printStackTrace();

            response = " ";
            statusCode = 1;
        }
//        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//            response = gson.toJson("Не удалось распознать идентификатор " + e.getMessage());
//            statusCode = 407;
//
//        } catch (IllegalArgumentException | JsonSyntaxException e) {
//            response = gson.toJson("Неверно указаны данные " + e.getMessage());
//            statusCode = 400;
//
//        } catch (NotFoundException e) {
//            response = gson.toJson(e.getMessage());
//            statusCode = 404;
//
//        } catch (InCorrectClassException e) {
//            response = gson.toJson(e.getMessage());
//            statusCode = 400;
//
//        }
        sendResponse(exchange, statusCode, response);
    }

    public void chekListOfTasks() {
        if (taskManager.getTasks(Type.TASK).isEmpty()) {
            throw new NotFoundException("Списка Tasks пуст");
        }
    }

    private void validateTaskType(T task) {
        if (task.getType() != Type.TASK) {
            throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Task");
        }
    }
}

package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.SubTask;
import model.Task;
import model.Type;
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

        int statusCode = -1;
        String response = "";

        try {
            switch (method) {
                case "GET" -> {
                    if (queryString != null) {
                        int id = parseIdFromQuery(queryString);
                        Task task = getTask(id);
                        response = gson.toJson(task);
                    } else {
                        List<T> tasks = getListOfTasks();
                        response = gson.toJson(tasks);
                    }
                    statusCode = 200;
                }
                case "POST" -> {
                    InputStream is = exchange.getRequestBody();
                    String stringJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    Task taskJson = gson.fromJson(stringJson, Task.class);

                    if (taskJson.getType() != Type.TASK){
                        throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Task");
                    }
                    if (taskJson.getId() != null && taskJson.getId() != 0) {
                        taskManager.updateTask((T) taskJson, taskJson.getId());
                        Task updateTask = getTask(taskJson.getId());

                        response = gson.toJson(updateTask);
                        statusCode = 200;
                    } else {
                        Task task = new Task(taskJson.getName(), taskJson.getDescription());
                        taskManager.add((T) task);
                        response = gson.toJson(task);
                        statusCode = 201;
                    }
                }
                case "DELETE" -> {
                    if (queryString != null) {
                        int id = parseIdFromQuery(queryString);
                        T task = getTask(id);
                        validateTaskType(task);

                        taskManager.removeTaskById(id);
                        response = gson.toJson("Задача с идентификатором " + id + " удалена.");

                    } else {
                        taskManager.removeTasks();
                        response = gson.toJson("Задачи удалены ");
                    }
                    statusCode = 200;
                }
                default -> {
                    response = gson.toJson("Не удалось распознать запрос");
                    statusCode = 501;
                }
            }

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            response = gson.toJson("Не удалось распознать идентификатор " + e.getMessage());
            statusCode = 400;

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            response = gson.toJson("Неверно указаны данные " + e.getMessage());
            statusCode = 400;

        } catch (NotFoundException e) {
            response = gson.toJson(e.getMessage());
            statusCode = 404;

        } catch (InCorrectClassException e) {
            response = gson.toJson(e.getMessage());
            statusCode = 400;

        }

        SubTaskHandler.sendResponse(exchange, statusCode, response);

    }

    public List<T> getListOfTasks() {
        List<T> tasks = taskManager.getTasks();
        if (tasks == null) {
            throw new NotFoundException("Список задач пуст");
        }
        return tasks;
    }

    private T getTask(int id) {
        T task = taskManager.getTaskById(id, false);
        if (task == null) {
            throw new NotFoundException("Задача не существует");
        }
        return task;
    }

    private static int parseIdFromQuery(String queryString) {
        String[] string = queryString.split("=");
        return Integer.parseInt(string[1]);
    }

    private void validateTaskType(T task) {
        if (task instanceof Epic || task instanceof SubTask) {
            throw new InCorrectClassException("Неверный тип задачи. Ожидаемый тип Task");
        }
    }
}

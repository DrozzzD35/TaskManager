package client;

import com.google.gson.Gson;
import model.Task;
import model.Type;
import server.InCorrectClassException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpClient<T extends Task> {
    private final java.net.http.HttpClient client;
    private final Gson gson = new Gson();
    private final String url = "http://localhost:8080/";
    private final String urlTask = "tasks/task";
    private final String urlEpic = "tasks/epic";
    private final String urlSubTask = "tasks/subtask";
    private final String urlHistory = "tasks/history";
    private final String urlTasks = "tasks";
    private final String urlId = "?id=";
    private String fullUrl;
    private Map<String, String> task;
    private String json;

    public HttpClient(java.net.http.HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> getTaskById(int id, Type type) throws IOException, InterruptedException {
        fullUrl = getFullUrlTaskById(type, id);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTasks(Type type) throws IOException, InterruptedException {
        fullUrl = getFullUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> updateTask(Type type, int id, String name, String description) throws IOException, InterruptedException {
        fullUrl = getFullUrlTaskById(type, id);

        Map<String, String> task = new HashMap<>();
        json = getJson(task, name, description);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> createTask(Type type, String name, String description) throws IOException, InterruptedException {
        task = new HashMap<>();
        json = getJson(task, name, description);
        fullUrl = getFullUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> createSubTask(Type type, String name, String description, int parentId) throws IOException, InterruptedException {
        task = new HashMap<>();
        task.put("parentId", String.valueOf(parentId));
        json = getJson(task, name, description);
        fullUrl = getFullUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> removeTask(int id, Type type) throws IOException, InterruptedException {
        fullUrl = getFullUrlTaskById(type, id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .DELETE()
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> removeTasks(Type type) throws IOException, InterruptedException {
        fullUrl = getFullUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .DELETE()
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private String getJson(Map<String, String> task, String name, String description) {
        task.put("name", name);
        task.put("description", description);
        return gson.toJson(task);
    }

    private String getFullUrlTaskById(Type type, int id) {
        switch (type) {
            case TASK -> fullUrl = url + urlTask + urlId + id;
            case EPIC -> fullUrl = url + urlEpic + urlId + id;
            case SUBTASK -> fullUrl = url + urlSubTask + urlId + id;
            default -> throw new InCorrectClassException("Неизвестный тип задачи");
        }

        return fullUrl;
    }

    private String getFullUrlTasks(Type type) {
        switch (type) {
            case TASK -> fullUrl = url + urlTask;
            case EPIC -> fullUrl = url + urlEpic;
            case SUBTASK -> fullUrl = url + urlSubTask;
            case null -> fullUrl = url + urlTasks;
            default -> throw new InCorrectClassException("Неизвестный тип задачи");
        }

        return fullUrl;
    }


}

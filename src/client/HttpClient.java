package client;

import com.google.gson.Gson;
import model.Task;
import model.Type;
import server.Config;
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
    Config config = new Config();
    private final String urlTask = config.getUrl() + config.getTasks() + config.getTask();
    private final String urlEpic = config.getUrl() + config.getTasks() + config.getEpic();
    private final String urlSubTask = config.getUrl() + config.getTasks() + config.getSubTask();
    private final String urlHistory = config.getUrl() + config.getTasks() + config.getHistory();

    public HttpClient(java.net.http.HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> getHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlHistory))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTaskById(int id, Type type) throws IOException, InterruptedException {
        String fullUrl = getUrlTaskById(type, id);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTasks(Type type) throws IOException, InterruptedException {
        String fullUrl = getUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> updateTask(int id, T updatedTask) throws IOException, InterruptedException {
        Type type = updatedTask.getType();
        String fullUrl = getUrlTaskById(type, id);
        String json = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(fullUrl))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> createTask(Type type, String name, String description) throws IOException, InterruptedException {
        Map<String, String> task = new HashMap<>();
        return getResponse(type, name, description, task);
    }

    public HttpResponse<String> createSubTask(Type type, String name, String description, int parentId) throws IOException, InterruptedException {
        Map<String, String> task = new HashMap<>();
        task.put("parentId", String.valueOf(parentId));
        return getResponse(type, name, description, task);
    }

    public HttpResponse<String> removeTask(int id, Type type) throws IOException, InterruptedException {
        String fullUrl = getUrlTaskById(type, id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .DELETE()
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> removeTasks(Type type) throws IOException, InterruptedException {
        String fullUrl = getUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .DELETE()
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> removeHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlHistory))
                .DELETE()
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getResponse(Type type, String name, String description, Map<String, String> task) throws IOException, InterruptedException {
        String json = getJson(task, name, description);
        String fullUrl = getUrlTasks(type);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .POST(HttpRequest.BodyPublishers.ofString(json))
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

    private String getUrlTaskById(Type type, int id) {
        String urlId = "?id=";
        String url;
        switch (type) {
            case TASK -> url = urlTask + urlId + id;
            case EPIC -> url = urlEpic + urlId + id;
            case SUBTASK -> url = urlSubTask + urlId + id;
            default -> throw new InCorrectClassException("Неизвестный тип задачи");
        }

        return url;
    }

    private String getUrlTasks(Type type) {
        String url;
        switch (type) {
            case TASK -> url = urlTask;
            case EPIC -> url = urlEpic;
            case SUBTASK -> url = urlSubTask;
            case null -> url = config.getTasks();
            default -> throw new InCorrectClassException("Неизвестный тип задачи");
        }

        return url;
    }


}

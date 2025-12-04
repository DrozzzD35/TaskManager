package client;

import com.google.gson.Gson;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpClient<T extends Task> {
    private final java.net.http.HttpClient client;
    private final String url = "http://localhost:8080/";
    private final String urlTask = "tasks/task";
    private final String urlId = "?id=";
    private final Gson gson = new Gson();

    public HttpClient(java.net.http.HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> createTaskClient(String name, String description) throws IOException, InterruptedException {
        Map<String, String> task = new HashMap<>();
        task.put("name", name);
        task.put("description", description);
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTaskByIdClient(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + urlTask + urlId + id))
                .header("Content-Type"
                        , "application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getTasksClient() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + urlTask))
                .header("Content-Type"
                ,"application/json; Charset=UTF-8")
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }



}

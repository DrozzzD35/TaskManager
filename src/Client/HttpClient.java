package Client;

import model.Task;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClient<T extends Task> {
    private final java.net.http.HttpClient client;
    private final String uri = "http://localhost:8080/"

    public HttpClient(java.net.http.HttpClient client) {
        this.client = client;
    }

//    public HttpResponse<T> createTask(String name, String description){
//        HttpRequest request = HttpRequest.newBuilder()
//
//
//    }

}

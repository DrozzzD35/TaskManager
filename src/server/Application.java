package server;

import model.Task;
import service.HttpTaskServer;

import java.io.IOException;

public class Application {

    public static <T extends Task> void main(String[] args) throws IOException {
        HttpTaskServer<T> httpTaskServer = new HttpTaskServer<>();
        httpTaskServer.start();
    }
}

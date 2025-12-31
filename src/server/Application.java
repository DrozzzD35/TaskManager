package server;

import service.HttpTaskServer;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}

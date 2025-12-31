package server;

import service.HttpTaskServer;

import java.io.IOException;
import java.nio.file.Path;

public class Application {

    public static void main(String[] args) throws IOException {
//        Path path = Path.of("src/dataBacked/FileBacked.CSV");
//        TaskManager<Task> fileBackedTasksManager = Managers.getDefaultFile(path);
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}

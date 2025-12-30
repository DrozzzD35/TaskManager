package service;

import model.Task;
import server.HttpTaskManager;

import java.nio.file.Path;

public final class Managers {

    private static TaskManager inMemoryTaskManager;
    private static TaskManager fileBackedTasksManager;
    private static HistoryManager inMemoryHistoryManager;
    private static HttpTaskManager httpTaskManager;

    private Managers() {
        throw new UnsupportedOperationException("Утилитарный класс");
    }

//    public static <T extends Task> TaskManager<T> getDefault() {
//        if (httpTaskManager == null) {
//            httpTaskManager = new HttpTaskManager();
//        }
//        return httpTaskManager;
//    }

    public static <T extends Task> TaskManager<T> getDefaultFile(Path filePath) {
        if (fileBackedTasksManager == null) {
            fileBackedTasksManager = new FileBackedTasksManager<T>(filePath);
        }
        return fileBackedTasksManager;
    }

    public static <T extends Task> HistoryManager<T> getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager<T>();
        }
        return inMemoryHistoryManager;
    }

}

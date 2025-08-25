package service;

import model.Task;

import java.nio.file.Path;

public final class Managers {

    private Managers() {
        throw new UnsupportedOperationException("Утилитарный класс");
    }

    public static <T extends Task> TaskManager<T> getDefault() {
        return new InMemoryTaskManager<>();
    }

    public static <T extends Task> TaskManager<T> getDefaultFile(Path filePath) {
        return new FileBackedTasksManager<>(filePath);
    }

    public static <T extends Task> HistoryManager<T> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }

}

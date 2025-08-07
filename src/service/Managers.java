package service;

import model.Task;

import java.util.LinkedList;

public final class Managers {

    private Managers() {
        throw new UnsupportedOperationException("Утилитарный класс");
    }

    public static <T extends Task> TaskManager<T> getDefault(HistoryManager<T> manager) {
        return new InMemoryTaskManager<>(manager);
    }

    public static <T extends Task>
    HistoryManager<T> getDefaultHistory() {
        return new InMemoryHistoryManager<>();
    }

}

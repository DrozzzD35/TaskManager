package service;

import model.Task;

public final class Managers {

    private Managers() {
        throw new UnsupportedOperationException("Утилитарный класс");
    }

    public static <T extends Task> TaskManager<T> getDefault() {
        return new InMemoryTaskManager<>();
    }
}

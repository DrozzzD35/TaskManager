package dataStorage;

import model.Task;
import service.InMemoryHistoryManager;
import service.TaskManager;

import java.io.File;
import java.nio.file.Path;

public class FileBackedTasksManager<T extends Task> extends InMemoryHistoryManager<T> implements TaskManager<T> {
    private final Path saveFile;

    public FileBackedTasksManager(Path saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public void add(T task) {
        super.add(task);
    }
}

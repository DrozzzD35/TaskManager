package dataBacked;

import model.Task;
import service.InMemoryHistoryManager;
import service.TaskManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManager<T extends Task> extends InMemoryHistoryManager<T> implements TaskManager<T> {
    private final Path saveFile;

    public FileBackedTasksManager(Path saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public void add(T task) {
        super.add(task);
        save(task);
    }

    public void save(T task) {
        try (BufferedWriter writer = Files.newBufferedWriter(saveFile)) {
            writer.append("id, name, description, taskStatus, type, children(id, name)\n");
            writer.append(String.valueOf(task.getId()))
                    .append(", ")
                    .append(task.getName())
                    .append(", ")
                    .append(task.getDescription())
                    .append(", ")
                    .append(String.valueOf(task.getStatus()))
                    .append(", ")
                    .append(String.valueOf(task.getType()))
                    .append("\n");

            System.out.println("CSV файл успешно создан " + saveFile);

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }
}

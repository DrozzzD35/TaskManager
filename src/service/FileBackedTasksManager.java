package service;

import dataBacked.ManagerSaveException;
import model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManager<T extends Task> extends InMemoryTaskManager<T> {

    private final Path filePath;
    private static final String csvHeaderText = "id, name, description, taskStatus, type, epic_id\n";

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void add(T task) {
        super.add(task);
        save();
    }

    @Override
    public void updateTask(T updateTask, int id) {
        super.updateTask(updateTask, id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    public void save() {
//        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
//            writer.append("id, name, description, taskStatus, type, children(id, name)\n");
//            writer.append(String.valueOf(task.getId()))
//                    .append(", ")
//                    .append(task.getName())
//                    .append(", ")
//                    .append(task.getDescription())
//                    .append(", ")
//                    .append(String.valueOf(task.getStatus()))
//                    .append(", ")
//                    .append(String.valueOf(task.getType()))
//                    .append("\n");
//
//            System.out.println("CSV файл успешно создан " + filePath);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка " + e);
//        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.append(csvHeaderText);

            for (T task : getTasks()) {
                writer.append(toString(task)).append("\n");
            }

            writer.append("\n");
            for (Task task : history.getHistory()) {
                writer.append(toString(task)).append("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранение файла " + e);
        }

        System.out.println("CSV файл успешно создан " + filePath);

    }


    public static Task fromString(String line) {
        String[] text = line.split(",");
        Task task = new Task(text[0], text[1]);
        task.setType(Type.valueOf(text[2]));
        task.setStatus(TaskStatus.valueOf(text[3]));


        return task;
    }


    private String toString(Task task) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(task.getId()).append(", ").append(task.getName()).append(", ").append(task.getDescription()).append(", ").append(task.getStatus()).append(", ").append(task.getType());
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            resultBuilder.append(", ").append(epic.getAllChildrenId());
        }
        resultBuilder.append("\n");

        return resultBuilder.toString();
    }


}

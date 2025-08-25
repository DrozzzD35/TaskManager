package dataBacked;

import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    public List<T> getTasks() {
        return List.of();
    }

    @Override
    public T getTaskById(int id) {
        return null;
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

        try(BufferedWriter writer = Files.newBufferedWriter(filePath)){
            writer.append(csvHeaderText);

            for (T task : getTasks()){
                writer.append(toString(task)).append("\n");
            }

            writer.append("\n");
            for (Task task : history.getHistory()){

            }


        } catch (IOException e) {
            throw new RuntimeException("Ошибка " + e);
        }

    }

    private String toString(Task task){
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder
                .append(task.getId()).append(", ")
                .append(task.getName()).append(", ");

        if (task instanceof SubTask){

        }

        return resultBuilder.toString();
    }


}

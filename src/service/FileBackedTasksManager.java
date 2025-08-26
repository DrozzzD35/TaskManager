package service;

import dataBacked.ManagerSaveException;
import model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTasksManager<T extends Task> extends InMemoryTaskManager<T> {

    private final Path filePath;
    private static final String csvHeaderText = "id, type, name, status, description, epic_id\n";

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

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.append(csvHeaderText);

            for (T task : getTasks()) {
                try {
                    writer.append(toString(task)).append("\n");
                } catch (ManagerSaveException e) {
                    System.out.println("Проблема с добавлением таска: " + task);
                }
            }

            writer.append("\n");


            List<T> tasksHistory = history.getHistory();
            String[] ids = new String[tasksHistory.size()];

            for (int i = 0; i < tasksHistory.size(); i++) {
                ids[i] = String.valueOf(tasksHistory.get(i).getId());
            }

            writer.append(String.join(",", ids));

        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка при сохранение файла " + e);
        }

        System.out.println("CSV файл успешно создан " + filePath);

    }


    public Task fromString(String line) {
        String[] values = line.split(", ");
        switch (Type.valueOf(values[1])) {
            case TASK -> {
                return new Task(Integer.valueOf(values[0]), Type.valueOf(values[1])
                        , values[2], TaskStatus.valueOf(values[3]), values[4]);
            }
            case EPIC -> {
                return new Epic(Integer.valueOf(values[0]), Type.valueOf(values[1])
                        , values[2], TaskStatus.valueOf(values[3]), values[4]);
            }
            case SUBTASK -> {
                InMemoryTaskManager<T> inMemoryTaskManager = new InMemoryTaskManager<>();
                Epic epic = (Epic) inMemoryTaskManager.getTaskById(Integer.parseInt(values[5]));
                return new SubTask(Integer.valueOf(values[0]), Type.valueOf(values[1])
                        , values[2], TaskStatus.valueOf(values[3]), values[4], epic);
            }
            default -> {
                return null;
            }
        }

    }


    private String toString(Task task) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder
                .append(task.getId()).append(", ")
                .append(task.getType()).append(", ")
                .append(task.getName()).append(", ")
                .append(task.getStatus()).append(", ")
                .append(task.getDescription());

        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            try {
                resultBuilder.append(", ").append(subTask.getParent().getId());
            } catch (Exception e) {
                throw new ManagerSaveException("Ошибка, у SubTask отсутствует Epic " + e);
            }
        }
        resultBuilder.append("\n");

        return resultBuilder.toString();
    }


}

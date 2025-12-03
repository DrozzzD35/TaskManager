package service;

import dataBacked.FileSaveException;
import dataBacked.ManagerSaveException;
import model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

    @Override
    public T getTaskById(int id, boolean withHistory) {
        T task = super.getTaskById(id, withHistory);
        save();
        return task;
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.append(csvHeaderText);

            for (T task : getAllTasks()) {
                try {
                    writer.append(toString(task)).append("\n");
                } catch (ManagerSaveException e) {
                    System.out.println("Проблема с добавлением таска: " + task);
                }
            }

            writer.append("\n");

            String ids = historyToString();

            writer.append(String.join(",", ids));
        } catch (Exception e) {
            throw new ManagerSaveException(e.getMessage());
        }

        System.out.println("CSV файл успешно создан " + filePath);
        System.out.println();

    }

    private String historyToString() {
        List<T> tasksHistory = history.getHistory();

        System.out.println("Размер списка history: " + tasksHistory.size());

        String[] ids = new String[tasksHistory.size()];

        for (int i = 0; i < tasksHistory.size(); i++) {
            ids[i] = String.valueOf(tasksHistory.get(i).getId());
        }

        System.out.println("Сохранённая история: " + String.join(",", ids));


        return String.join(",", ids);
    }


    public static Task fromString(String line) {
        String[] values = line.split(", ");
        switch (Type.valueOf(values[1])) {
            case TASK -> {
                return new Task(Integer.valueOf(values[0]), Type.valueOf(values[1]), values[2], TaskStatus.valueOf(values[3]), values[4]);
            }
            case EPIC -> {
                return new Epic(Integer.valueOf(values[0]), Type.valueOf(values[1]), values[2], TaskStatus.valueOf(values[3]), values[4]);
            }
            case SUBTASK -> {
                return new SubTask(Integer.valueOf(values[0]), Type.valueOf(values[1]), values[2], TaskStatus.valueOf(values[3]), values[4], Integer.parseInt(values[5]));
            }
            default -> {
                return null;
            }
        }

    }


    public static TaskManager<Task> loadFromFile(Path path) {
        TaskManager<Task> manager = new FileBackedTasksManager<>(path);
        try {
            String fileContent = Files.readString(path);
            String[] lines = fileContent.split("\n");
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    String historyLine = lines[i + 1];

                    Integer[] ids =Arrays.stream(historyLine.split(","))
                            .map(Integer::valueOf)
                            .toArray(Integer[]::new);

                    manager.addTaskByIdsToHistory(ids);
                    break;
                } else {
                    Task task = fromString(lines[i]);
                    if (task != null) {
                        manager.add(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new FileSaveException(e.getMessage());
        }

        return manager;
    }

    private String toString(Task task) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(task.getId()).append(", ").append(task.getType().name())
                .append(", ").append(task.getName())
                .append(", ").append(task.getStatus().name())
                .append(", ").append(task.getDescription());

        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            try {
                resultBuilder.append(", ").append(subTask.getParentId());
            } catch (Exception e) {
                throw new ManagerSaveException("у SubTask отсутствует Epic " + e.getMessage());
            }
        }

        return resultBuilder.toString();
    }


}

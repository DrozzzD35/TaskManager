package service;

import dataBacked.FileSaveException;
import dataBacked.ManagerSaveException;
import model.*;
import utils.GsonFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FileBackedTasksManager<T extends Task> extends InMemoryTaskManager<T> {
    private final Path filePath;
    private static final String csvHeaderText
            = "id, type, name, status, description, startTime, duration, epic_id\n";

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void add(T task) {
        super.add(task);
        save();
        System.out.println("CSV файл успешно создан " + filePath + "\n");
    }

    public void addWithoutSave(T task) {
        super.add(task);
    }

    @Override
    public void updateTask(T newTask, int id) {
        super.updateTask(newTask, id);
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
    }

    private String historyToString() {
        List<T> tasksHistory = history.getHistory();
        String[] ids = new String[tasksHistory.size()];

        for (int i = 0; i < tasksHistory.size(); i++) {
            ids[i] = String.valueOf(tasksHistory.get(i).getId());
        }

        return String.join(",", ids);
    }

    public static Task fromString(String line) {
        String[] values = line.split(", ");
        LocalDateTime start = values[5].equals("null")
                ? null : LocalDateTime.parse(values[5], GsonFactory.DATE_TIME_FORMATTER);

        switch (Type.valueOf(values[1])) {
            case TASK -> {
                return new Task(Integer.valueOf(values[0])
                        , Type.valueOf(values[1])
                        , values[2], TaskStatus.valueOf(values[3])
                        , values[4], start
                        , Duration.ofMinutes(Long.parseLong(values[6])));
            }
            case EPIC -> {
                return new Epic(Integer.valueOf(values[0])
                        , Type.valueOf(values[1]), values[2]
                        , TaskStatus.valueOf(values[3])
                        , values[4], start
                        , Duration.ofMinutes(Long.parseLong(values[6])));
            }
            case SUBTASK -> {
                return new SubTask(Integer.valueOf(values[0])
                        , Type.valueOf(values[1]), values[2]
                        , TaskStatus.valueOf(values[3]), values[4]
                        , start
                        , Duration.ofMinutes(Long.parseLong(values[6]))
                        , Integer.parseInt(values[7]));
            }
            default -> {
                return null;
            }
        }

    }

    public static TaskManager<Task> loadFromFile(Path path) {
        FileBackedTasksManager<Task> manager = new FileBackedTasksManager<>(path);

        try {
            String fileContent = Files.readString(path);
            String[] lines = fileContent.split("\n");

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    if (i + 1 < lines.length) {
                        String historyLine = lines[i + 1];

                        Integer[] ids = Arrays.stream(historyLine.split(","))
                                .map(Integer::valueOf)
                                .toArray(Integer[]::new);

                        manager.addTaskByIdsToHistory(ids);
                    }
                    break;
                } else {
                    Task task = fromString(lines[i]);
                    if (task != null) {
                        manager.addWithoutSave(task);
                    }
                }
            }

            for (Task epic : manager.getTasks(Type.EPIC)) {
                manager.updateEpicStatus(epic.getId());
                manager.updateEpicTime(epic.getId());
            }
        } catch (IOException e) {
            throw new FileSaveException(e.getMessage());
        }

        return manager;
    }

    private String toString(Task task) {
        String timeString = (task.getStartTime() != null)
                ? task.getStartTime().format(GsonFactory.DATE_TIME_FORMATTER) : "null";

        long durationString = (task.getDuration() != null)
                ? task.getDuration().toMinutes() : 0;

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(task.getId()).append(", ")
                .append(task.getType().name())
                .append(", ").append(task.getName())
                .append(", ").append(task.getStatus().name())
                .append(", ").append(task.getDescription())
                .append(", ").append(timeString)
                .append(", ").append(durationString);


        if (task instanceof SubTask subTask) {
            try {
                resultBuilder.append(", ").append(subTask.getParentId());
            } catch (Exception e) {
                throw new ManagerSaveException("у SubTask отсутствует Epic " + e.getMessage());
            }
        }

        return resultBuilder.toString();
    }


}

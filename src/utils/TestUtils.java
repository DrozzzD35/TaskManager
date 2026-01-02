package utils;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class TestUtils {

    private TestUtils() {
        System.out.println("Утилитарный класс");
    }

    public static void addHistory(TaskManager<Task> taskManager, Task task) {
        if (task == null) {
            return;
        }
        taskManager.getTaskById(task.getId(), true);
        System.out.println("Задача добавлена в историю, id = " + task.getId() );
    }

    public static void updateTask(TaskManager<Task> taskManager, int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updateTask, currentTaskId);
    }

    public static Task createTask(TaskManager<Task> taskManager, String name, String description, String date, int minutes) {
        Task task = new Task(name, description
                , LocalDateTime.parse(date, GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(minutes));
        taskManager.add(task);
        return task;
    }

    public static SubTask createSubTask(TaskManager<Task> taskManager, String name, String description, String date, int minutes, int epicId) {
        if (!(taskManager.getTaskById(epicId, false) instanceof Epic)) {
            System.out.println(("Невозможно создать SubTask, Epic с ID:" + epicId + " не существует"));
            return null;
        }

        SubTask subTask = new SubTask(name, description
                , LocalDateTime.parse(date, GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(minutes)
                , epicId);
        taskManager.add(subTask);
        return subTask;
    }

    public static Epic createEpic(TaskManager<Task> taskManager, String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.add(epic);
        return epic;
    }
}

package utils;

import model.*;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class TestUtils {

    private TestUtils() {
        System.out.println("Утилитарный класс");
    }

    public static <T extends Task> void addHistory(TaskManager<T> taskManager, Task task) {
        if (task == null) {
            return;
        }
        taskManager.getTaskById(task.getId(), true);
        System.out.println("Задача добавлена в историю, id = " + task.getId());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> void updateTask(TaskManager<T> taskManager, int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask((T) updateTask, currentTaskId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> Task createTask(TaskManager<T> taskManager
            , String name, String description, String date, int minutes) {

        Task task = new Task(name, description
                , LocalDateTime.parse(date, GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(minutes));
        taskManager.add((T) task);
        return task;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> SubTask createSubTask(TaskManager<T> taskManager
            , String name, String description, String date, int minutes, int epicId) {

        if (!(taskManager.getTaskById(epicId, false) instanceof Epic)) {
            System.out.println(("Невозможно создать SubTask, Epic с ID:" + epicId + " не существует"));
            return null;
        }

        SubTask subTask = new SubTask(name, description
                , LocalDateTime.parse(date, GsonFactory.DATE_TIME_FORMATTER)
                , Duration.ofMinutes(minutes)
                , epicId);
        taskManager.add((T) subTask);
        return subTask;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> Epic createEpic(TaskManager<T> taskManager, String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.add((T) epic);
        return epic;
    }

    public static <T extends Task> SubTask createSubTask(TaskManager<T> taskManager, String dateTime, Integer parentId) {
        return TestUtils.createSubTask(taskManager, "task2", "task2"
                , dateTime
                , 20
                , parentId);
    }

    public static <T extends Task> Task createTask(TaskManager<T> taskManager, String date) {
        return TestUtils.createTask(taskManager, "name1", "description1"
                , date, 50);
    }
}

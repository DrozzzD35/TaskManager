import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.nio.file.Path;

public class MyTest {

    private static TaskManager<Task> manager;
    private static TaskManager<Task> saveManager;

    public static void main(String[] args) {
        Path path = Path.of("src/dataBacked/FileBacked.CSV");
        manager = Managers.getDefault();
        saveManager = Managers.getDefaultFile(path);


        // Создание задачи
        Task task1 = createTask("name10", "description10");
        Task task2 = createTask("name11", "description11");
        Task task3 = createTask("name12", "description12");

        // Цикл создания задач
        taskCreationCycle(23);

        // Обновление задачи
        updateTaskStatus(task1.getId());

        System.out.println("===========  Таски в памяти   ===============");
        System.out.println(manager.getTasks());

        System.out.println("==========================\n\n");

        // Добавление задачи в историю
        addHistory(task1);
        addHistory(task2);
        addHistory(task3);

        // Цикл добавления задач в историю
        addHistoryCycle(12);


        System.out.println("===========  История   ===============");
        System.out.println("История " + manager.getHistory());
        System.out.println("=========================\n\n");


    }

    private static void taskCreationCycle(int quantity) {
        for (int i = 0; i <= quantity; i++) {
            createTask("name" + i, "description" + i);

        }
    }

    private static void addHistory(Task task) {
        manager.getTaskById(task.getId());
    }

    private static void updateTaskStatus(int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updateTask, currentTaskId);
    }

    private static Task createTask(String name, String description) {
        Task task = new Task(name, description);
        manager.add(task);
        return task;
    }

    private static void addHistoryCycle(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            manager.getTaskById(i);

        }
    }

}

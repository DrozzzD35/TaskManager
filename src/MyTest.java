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


        Task task4 = createSaveTask();

        // Создание задачи
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();

        updateTaskStatus(task1.getId());

        System.out.println("===========  Таски в памяти   ===============");
        System.out.println(manager.getTasks());

        System.out.println("==========================\n\n");

        addTaskHistory(task1);


        System.out.println("===========  История   ===============");
        System.out.println("История " + manager);
        System.out.println("=========================\n\n");

//        manager.updateTask(updateTask1, task1.getId());
//
//
//        System.out.println(manager.getTaskById(task1.getId()));
//        System.out.println(manager.getTasks());
//        System.out.println();


        //Тест истории
//        Task task2 = new Task("task2", "task2");
//        manager.add(task2);
//        manager.add(new Task("t3", "t3"));
//        manager.add(new Task("t4", "t4"));
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task2.getId());
//        manager.getTaskById(task1.getId());
//        manager.getTaskById(task2.getId());
//        System.out.println("Все задачи: ");
//        System.out.println(manager.getTasks());

//        System.out.println();
//        System.out.println("История " + history.getHistory());


    }

    private static Task createSaveTask() {
        Task task = new Task("saveTask", "saveTask");
        saveManager.add(task);
        return task;
    }

    private static void addTaskHistory(Task task) {
        manager.getTaskById(task.getId());
    }

    private static void updateTaskStatus(int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updateTask, currentTaskId);
    }

    private static Task createTask() {
        Task task = new Task("task", "task");
        manager.add(task);
        return task;
    }

}

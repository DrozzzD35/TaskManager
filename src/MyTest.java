import model.Status;
import model.Task;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTest {

    public static void main(String[] args) {
        TaskManager<Task> manager = new InMemoryTaskManager<>();

        // Создание задчи
        Task task1 = new Task("task1", "task1");
        manager.add(task1);
        Task existTask1 = manager.getTaskById(task1.getId());
        System.out.println(existTask1);

        // Обновление задачи
        Task updateTask1 = new Task("updateTask1", "updateTask1");
        updateTask1.setStatus(Status.IN_PROGRESS);

        manager.updateTask(updateTask1, task1.getId());


        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println(manager.getTasks());
        System.out.println();

        Task task2 = new Task("task2", "task2");
        manager.add(task2);
        manager.add(new Task("t3", "t3"));
        manager.add(new Task("t4", "t4"));
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());


        System.out.println("Все задачи: ");
        System.out.println(manager.getTasks());
        System.out.println();

        System.out.println("История " + manager.getHistory());


    }

}

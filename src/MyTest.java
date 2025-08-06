import model.TaskStatus;
import model.Task;
import service.*;

import java.util.LinkedList;

public class MyTest {

    public static void main(String[] args) {
        TaskManager<Task> manager = Managers.getDefault();
        HistoryManager<Task> historyManager = Managers.getDefaultHistory();
        TaskManager<Task> inMemoryTaskManager = new InMemoryTaskManager<>(historyManager);

        // Создание задачи
        Task task1 = new Task("task1", "task1");
        manager.add(task1);
        Task existTask1 = manager.getTaskById(task1.getId());
        System.out.println(existTask1);

        // Обновление задачи
        Task updateTask1 = new Task("updateTask1", "updateTask1");
        updateTask1.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateTask(updateTask1, task1.getId());


        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println(manager.getTasks());
        System.out.println();

        //Тест истории
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
//        System.out.println("Все задачи: ");
//        System.out.println(manager.getTasks());

        System.out.println();
        System.out.println("История " + historyManager.getHistory());


    }

}

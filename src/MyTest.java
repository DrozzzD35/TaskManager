import model.Status;
import model.Task;
import service.InMemoryTaskManager;
import service.TaskManager;

public class MyTest {

    public static void main(String[] args) {
        TaskManager<Task> manager = new InMemoryTaskManager<>();

        // Создание задчи
        Task task1 = new Task("task1", "task1");
        manager.add(task1);
        Task existTask1 =  manager.getTaskById(task1.getId());
        System.out.println(existTask1);

        // Обновление задачи
        Task updateTask1 = new Task("updateTask1", "updateTask1");
        updateTask1.setStatus(Status.IN_PROGRESS);

        manager.updateTask(updateTask1, task1.getId());


        System.out.println(manager.getTaskById(task1.getId()));
        System.out.println( manager.getTasks());
    }

}

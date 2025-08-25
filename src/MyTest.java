import model.Task;
import model.TaskStatus;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

public class MyTest {

    private static TaskManager<Task> manager;

    public static void main(String[] args) {
        manager = Managers.getDefault();



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
        System.out.println("История " + manager. getHistory());
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

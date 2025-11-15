import model.Epic;
import model.SubTask;
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


//        Task task8 = createTask("name10", "description10");
//        saveManager.getTaskById(task8.getId(), true);
//
//        System.out.println(saveManager.getTasks());


        // Создание задачи
        Task task1 = createTask("name10", "description10");
        Epic task2 = createEpic("epic11", "epic11");
        SubTask task3 = createSubTask("subTask12", "subTask12", 2);
        Epic task4 = createEpic("epic13", "epic13");
        SubTask task5 = createSubTask("subTask14", "subTask14", 4);
        Epic task6 = createEpic("epic15", "epic15");
        SubTask task7 = createSubTask("subTask16", "subTask16", 6);


        // Цикл создания задач
        taskCreationCycle(5);

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
        addHistoryCycle(2);


        System.out.println("===========  История   ===============");
        System.out.println("История " + manager.getHistory());
        System.out.println("=========================\n\n");


    }


    private static void taskCreationCycle(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            createTask("name" + i, "description" + i);

        }
    }

    private static void addHistory(Task task) {
        manager.getTaskById(task.getId(), true);
    }

    private static void updateTaskStatus(int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        saveManager.updateTask(updateTask, currentTaskId);
    }

    private static Task createTask(String name, String description) {
        Task task = new Task(name, description);
        manager.add(task);
        return task;
    }

    private static SubTask createSubTask(String name, String description, int epicId) {
        SubTask subTask = new SubTask(name, description, epicId);
        saveManager.add(subTask);
        return subTask;
    }

    private static Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        saveManager.add(epic);
        return epic;
    }

    private static void addHistoryCycle(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            manager.getTaskById(i, true);

        }
    }

}

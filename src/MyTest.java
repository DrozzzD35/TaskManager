import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.nio.file.Path;

public class MyTest {

    private static TaskManager<Task> saveManager;

    public static void main(String[] args) {
        Path path = Path.of("src/dataBacked/FileBacked.CSV");
        saveManager = Managers.getDefaultFile(path);

        // Создание задачи
        Task task1 = createTask("name10", "description10");
        Epic task2 = createEpic("epic11", "epic11");
        SubTask task3 = createSubTask("subTask12", "subTask12", 2);
        createEpic("epic13", "epic13");
        createSubTask("subTask14", "subTask14", 44);
        createEpic("epic15", "epic15");
        createSubTask("subTask16", "subTask16", 6);


        // Цикл создания задач
//        taskCreationCycle(20);

        // Обновление задачи
        System.out.println("===========  Обновление задачи   ===============");
        updateTaskStatus(task1.getId());
        System.out.println("==========================\n\n");

        // Вывод всех задач в консоль
        System.out.println("===========  Таски в памяти   ===============");
        System.out.println(saveManager.getAllTasks());
        System.out.println("==========================\n\n");

        // Добавление задачи в историю
        System.out.println("==================== Добавление задачи в историю ==================== ");
        addHistory(task1);
        addHistory(task2);
        addHistory(task3);

        // Цикл добавления задач в историю
//        addHistoryCycle(21);

        // Просмотр истории
        System.out.println("===========  История   ===============");
        System.out.println("История " + saveManager.getHistory());
        System.out.println("=========================\n");

        // Привидение типа, иначе метод save() недоступен
        ((FileBackedTasksManager<?>) saveManager).save();
        System.out.println("===========  Загрузка из файла   ===============");
        TaskManager<Task> restored = FileBackedTasksManager.loadFromFile(path);

        // Проверка на идентичность истории в памяти и в файле
        System.out.println("history ok? " + restored.getHistory().equals(saveManager.getHistory()));

    }

    private static void taskCreationCycle(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            createTask("name" + i, "description" + i);

        }
    }

    private static void addHistory(Task task) {
        if (task == null) {
            return;
        }
        saveManager.getTaskById(task.getId(), true);
    }

    private static void updateTaskStatus(int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        saveManager.updateTask(updateTask, currentTaskId);
    }

    private static Task createTask(String name, String description) {
        Task task = new Task(name, description);
        saveManager.add(task);
        return task;
    }

    private static SubTask createSubTask(String name, String description, int epicId) {
        if (!(saveManager.getTaskById(epicId, false) instanceof Epic)) {
            System.out.println(("Невозможно создать SubTask, Epic по ID:" + epicId + " не существует\n"));
            return null;
        }

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
            saveManager.getTaskById(i, true);
        }
    }

}

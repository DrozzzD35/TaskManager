import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyTest {

    private static TaskManager<Task> saveManager;

    public static void main(String[] args) {
        Path path = Path.of("src/dataBacked/FileBacked.CSV");
        saveManager = Managers.getDefaultFile(path);

        // Создание задачи
        Task task1 = createTask("name1", "description1", 40, "01.10.2020 00:00");
        Epic task2 = createEpic("epic2", "epic2");
        SubTask task3 = createSubTask("subTask3", "subTask3", "01.10.2020 01:00", 35, 2);
        createEpic("epic4", "epic4");
        createSubTask("subTask5", "subTask5", "01.10.2020 02:00", 20, 4);
        createEpic("epic6", "epic6");
        createSubTask("subTask7", "subTask7", "01.10.2020 03:00", 100, 6);
        createTask("name8", "description8", 50, "02.10.2020 00:00");
        createSubTask("subTask9", "subTask9", "02.10.2020 01:00", 100, 2);


        // Обновление задачи
        System.out.println("===========  Обновление задачи   ===============");
        updateTask(task1.getId());
        System.out.println("\n\n");

        // Вывод всех задач в консоль
        System.out.println("===========  Таски в памяти   ===============");
        System.out.println(saveManager.getAllTasks());
        System.out.println("\n\n");

        //удаление задачи
        System.out.println("===========  Удаление задачи   ===============");
        saveManager.removeTaskById(2);
        System.out.println();

        // Вывод всех задач в консоль
        System.out.println("===========  Таски после удаления   ===============");
        System.out.println(saveManager.getAllTasks());
        System.out.println("\n\n");

        // Добавление задачи в историю
        System.out.println("==================== Добавление задачи в историю ==================== ");
        addHistory(task1);
        addHistory(task2);
        addHistory(task3);


        // Просмотр истории
        System.out.println("===========  История   ===============");
        System.out.println("История " + saveManager.getHistory());
        System.out.println("=========================\n");

        // Привидение типа, иначе метод save() недоступен
        ((FileBackedTasksManager<?>) saveManager).save();
        System.out.println("===========  Загрузка из файла   ===============");
        TaskManager<Task> restored = FileBackedTasksManager.loadFromFile(path);

        // Проверка на идентичность истории в памяти и в файле
        System.out.println("history ok? " + restored.getHistory().equals(saveManager.getHistory()) + '\n');

        //Задачи в отсортированном списке
        System.out.println("===========  Задачи в отсортированном списке   ===============");
        System.out.println(((FileBackedTasksManager<Task>) saveManager).getPriorityzedTasks());
        System.out.println("=========================\n");


    }

    private static void addHistory(Task task) {
        if (task == null) {
            return;
        }
        saveManager.getTaskById(task.getId(), true);
    }

    private static void updateTask(int currentTaskId) {
        Task updateTask = new Task("updateTask", "updateTask");
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        saveManager.updateTask(updateTask, currentTaskId);
    }

    private static Task createTask(String name, String description, int minutes, String date) {
        DateTimeFormatter startTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Task task = new Task(name, description
                , LocalDateTime.parse(date, startTime)
                , Duration.ofMinutes(minutes));
        saveManager.add(task);
        return task;
    }

    private static SubTask createSubTask(String name, String description, String date, int minutes, int epicId) {
        if (!(saveManager.getTaskById(epicId, false) instanceof Epic)) {
            System.out.println(("Невозможно создать SubTask, Epic с ID:" + epicId + " не существует"));
            return null;
        }
        DateTimeFormatter startTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        SubTask subTask = new SubTask(name, description
                , LocalDateTime.parse(date, startTime)
                , Duration.ofMinutes(minutes)
                , epicId);
        saveManager.add(subTask);
        return subTask;
    }

    private static Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        saveManager.add(epic);
        return epic;
    }
}

package tests;

import model.Epic;
import model.SubTask;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;
import utils.TestUtils;

import java.io.IOException;
import java.nio.file.Path;

public class MyTest {

    public static void main(String[] args) throws IOException {
        Path path = Path.of("src/dataBacked/FileBacked.CSV");
        TaskManager<Task> saveManager = Managers.getDefaultFile(path);

        // Создание задачи
        Task task1 = TestUtils.createTask(saveManager, "name1", "description1", "01.10.2020 00:00", 40);
        Epic task2 = TestUtils.createEpic(saveManager, "epic2", "epic2");
        SubTask task3 = TestUtils.createSubTask(saveManager, "subTask3", "subTask3", "01.10.2020 01:00", 35, 2);
        TestUtils.createEpic(saveManager, "epic4", "epic4");
        TestUtils.createSubTask(saveManager, "subTask5", "subTask5", "01.10.2010 02:00", 20, 4);
        TestUtils.createEpic(saveManager, "epic6", "epic6");
        TestUtils.createSubTask(saveManager, "subTask7", "subTask7", "01.10.2020 03:00", 100, 6);
        TestUtils.createTask(saveManager, "name8", "description8", "02.10.2020 00:00", 50);
        TestUtils.createSubTask(saveManager, "subTask9", "subTask9", "02.10.2020 01:00", 100, 2);
        TestUtils.createSubTask(saveManager, "subTask10", "subTask10", "03.10.2020 01:00", 100, 2);


        // Обновление задачи
        System.out.println("===========  Обновление задачи   ===============");
        TestUtils.updateTask(saveManager, task1.getId());
        System.out.println("=========================\n");

        // Вывод всех задач в консоль
        System.out.println("===========  Таски в памяти   ===============");
        System.out.println(saveManager.getAllTasks());
        System.out.println("=========================\n");

        //удаление задачи
        System.out.println("===========  Удаление задачи   ===============");
        saveManager.removeTaskById(9);
        System.out.println("=========================\n");

        // Вывод всех задач в консоль
        System.out.println("===========  Таски после удаления   ===============");
        System.out.println(saveManager.getAllTasks());
        System.out.println("=========================\n");

        // Добавление задачи в историю
        System.out.println("==================== Добавление задачи в историю ==================== ");
        TestUtils.addHistory(saveManager, task1);
        TestUtils.addHistory(saveManager, task2);
        TestUtils.addHistory(saveManager, task3);
        System.out.println("=========================\n");

        // Просмотр истории
        System.out.println("===========  История   ===============");
        System.out.println("История " + saveManager.getHistory());
        System.out.println("=========================\n");

        // Привидение типа, иначе метод save() недоступен
        ((FileBackedTasksManager<?>) saveManager).save();
        System.out.println("===========  Загрузка из файла   ===============");
        TaskManager<Task> restored = FileBackedTasksManager.loadFromFile(path);
        System.out.println("=========================\n");

        // Проверка на идентичность истории в памяти и в файле
        System.out.println("history ok? " + restored.getHistory().equals(saveManager.getHistory()) + '\n');

        //Задачи в отсортированном списке
        System.out.println("===========  Задачи в отсортированном списке   ===============");
        System.out.println((saveManager).getPrioritizedTasks());
        System.out.println("=========================\n");

//        kvServer.stop();
    }

}

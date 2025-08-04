import model.Epic;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager<Task> inMemoryTaskManager = new InMemoryTaskManager<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int answerUser = scanner.nextInt();

            switch (answerUser) {
                case 1: {
                    System.out.println("Какую задачу хотели бы создать: ");
                    System.out.println("1. Задача");
                    System.out.println("2. Большую задача");
                    System.out.println("3. Подзадача");

                    int answer = scanner.nextInt();
                    System.out.println("Название задачи: ");
                    String name = scanner.next();
                    System.out.println("Описание задачи: ");
                    String description = scanner.next();
                    System.out.println();

                    switch (answer) {
                        case 1 -> {
                            Task task = new Task(name, description);
                            inMemoryTaskManager.add(task);
                            System.out.println();
                            break;
                        }
                        case 2 -> {
                            Task task = new Epic(name, description);
                            inMemoryTaskManager.add(task);
                            System.out.println();
                            break;
                        }
                        case 3 -> {
                            System.out.println("Введите идентификатор Большой задачи к которой принадлежит подзадача: ");
                            inMemoryTaskManager.printEpicTasks();

                            int epicById = scanner.nextInt();
                            Task epic = inMemoryTaskManager.getTaskById(epicById);
                            if (epic instanceof Epic) {
                                Task subTask = new SubTask(name, description, (Epic) epic);
                                inMemoryTaskManager.add(subTask);
                                System.out.println();
                                break;
                            }
                            System.out.println("Найденная задача не является Большой задачей");
                            break;
                        }

                    }

                    break;
                }
                case 2: {
                    System.out.println("Введите идентификатор задачи для её удаления");
                    inMemoryTaskManager.printAllTasks();
                    int id = scanner.nextInt();
                    inMemoryTaskManager.removeTaskById(id);
                    System.out.println();
                    break;
                }
                case 3: {
                    System.out.println("Вы уверены что хотите удалить все задачи?");
                    System.out.println("1. Да");
                    System.out.println("2. Нет");
                    int answer = scanner.nextInt();
                    if (answer == 1) {
                        inMemoryTaskManager.removeAllTasks();

                    } else {
                        return;
                    }
                    System.out.println();

                    break;
                }
                case 4: {
                    System.out.println("Введите идентификатор задачи для её поиска");
                    int id = scanner.nextInt();
                    inMemoryTaskManager.printTask(id);
                    System.out.println();
                    break;
                }
                case 5: {
                    inMemoryTaskManager.printAllTasks();
                    break;
                }
                case 6: {

                    System.out.println("Введите идентификатор задачи");
                    int taskId = scanner.nextInt();
                    inMemoryTaskManager.printTask(taskId);
                    System.out.println();

                    System.out.println("Что хотите обновить?");
                    System.out.println("1. Имя");
                    System.out.println("2. Описание");
                    System.out.println("3. Тип");
                    System.out.println("4. Статус");
                    int answer = scanner.nextInt();
                    System.out.println();

                    switch (answer) {
                        case 1 -> {
                            System.out.println("Введите имя");
                            String name = scanner.next();
                            Task task = new Task(name, null);

                            inMemoryTaskManager.updateTask(task, taskId);
                        }


                    }

                    break;

                }
                case 7: {
                    return;
                }
                default: {
                    System.out.println("Введена неверная команда");
                    System.out.println();
                    break;
                }
            }


        }

    }


    public static void printMenu() {
        System.out.println("Введите команду (1-6): ");
        System.out.println("1. Создать задачу");
        System.out.println("2. Удалить задачу");
        System.out.println("3. Удалить все задача");
        System.out.println("4. Найти задачу");
        System.out.println("5. Получение списка всех задач");
        System.out.println("6. Обновить задачу");
        System.out.println("7. Выход");
        System.out.println();
    }


}
import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManagerImpl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManagerImpl<Task> taskManagerImpl = new TaskManagerImpl<>();
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
                            taskManagerImpl.add(task);
                            System.out.println();
                            break;
                        }
                        case 2 -> {
                            Task task = new Epic(name, description);
                            taskManagerImpl.add(task);
                            System.out.println();
                            break;
                        }
                        case 3 -> {
                            System.out.println("Введите идентификатор Большой задачи к которой принадлежит подзадача: ");
                            taskManagerImpl.printEpicTasks();

                            int epicById = scanner.nextInt();
                            Task epic = taskManagerImpl.getTaskById(epicById);
                            if (epic instanceof Epic) {
                                Task subTask = new SubTask(name, description, (Epic) epic);
                                taskManagerImpl.add(subTask);
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
                    taskManagerImpl.printAllTasks();
                    int id = scanner.nextInt();
                    if (taskManagerImpl.getTaskById(id) instanceof Epic) {
                        System.out.println("Если удалить Большую задачу, подзадачи так же будут удалены");
                        System.out.println("Вы уверены что хотите удалить Большую задачу и все её подзадачи?");
                        System.out.println("1. Да");
                        System.out.println("2. Нет");
                        int answer = scanner.nextInt();
                        if (answer == 1) {
                            taskManagerImpl.removeTaskById(id);


                        } else {
                            return;
                        }

                    }
                    taskManagerImpl.removeTaskById(id);
                    System.out.println();
                    break;
                }
                case 3: {
                    System.out.println("Вы уверены что хотите удалить все задачи?");
                    System.out.println("1. Да");
                    System.out.println("2. Нет");
                    int answer = scanner.nextInt();
                    if (answer == 1) {
                        taskManagerImpl.removeAllTasks();

                    } else {
                        return;
                    }
                    System.out.println();

                    break;
                }
                case 4: {
                    System.out.println("Введите идентификатор задачи для её поиска");
                    int id = scanner.nextInt();
                    taskManagerImpl.printTask(id);
                    System.out.println();
                    break;
                }
                case 5: {
                    taskManagerImpl.printAllTasks();
                    break;
                }
                case 6: {

                    System.out.println("1. Создать задачу");
                    System.out.println("2. Создать большую задачу");
                    System.out.println("3. Создать подзадачу");

                    int answer = scanner.nextInt();
                    System.out.println("Название задачи: ");
                    String name = scanner.next();
                    System.out.println("Описание задачи: ");
                    String description = scanner.next();

                    switch (answer) {
                        case 1 -> {
                            Task task = new Task(name, description);
                            taskManagerImpl.updateTask(task);
                            System.out.println();
                            break;
                        }
                        case 2 -> {
                            Task task = new Epic(name, description);
                            taskManagerImpl.updateTask(task);
                            ;
                            System.out.println();
                            break;
                        }
                        case 3 -> {
                            System.out.println("Введите идентификатор Большой задачи к которой принадлежит подзадача: ");
                            taskManagerImpl.printEpicTasks();

                            int epicById = scanner.nextInt();
                            Task epic = taskManagerImpl.getTaskById(epicById);
                            if (epic instanceof Epic) {
                                Task subTask = new SubTask(name, description, (Epic) epic);
                                taskManagerImpl.add(subTask);
                                System.out.println();
                                break;
                            }
                            System.out.println("Найденная задача не является Большой задачей");
                            break;
                        }

                    }

                    break;

                }
                case 8: {
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
        System.out.println("8. Выход");
        System.out.println();
    }


}
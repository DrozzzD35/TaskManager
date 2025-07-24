import service.TaskManagerImpl;
import model.Task;
import test.Cat;
import test.Dog;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        TaskManagerImpl<Task> taskManagerImpl = new TaskManagerImpl<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int answerUser = scanner.nextInt();

            switch (answerUser) {
                case 1: {
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
                            taskManagerImpl.createTask(name, description);
                            System.out.println("Задачи создана успешно");
                            System.out.println();
                        }
                        case 2 -> {
                            taskManagerImpl.createEpicTask(name, description);
                            System.out.println("Большая задачи создана успешно");
                            System.out.println();
                        }
                        case 3 -> {
                            System.out.println("Введите id Большой задачи к которой хотите отнести подзадачу?");
                            taskManagerImpl.printListEpic();

                            int epicById = scanner.nextInt();
                            Task epic = taskManagerImpl.getTaskById(epicById);

                            taskManagerImpl.createSubTaskTask(name, description, (Task)epic);
                        }
                    }


                }
                case 2: {
                    System.out.println("Введите идентификатор задачи для её удаления");
                    int id = scanner.nextInt();
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

                    System.out.println("Введите идентификатор задачи: ");
                    int id = scanner.nextInt();
                    Task task = taskManagerImpl.findTask(id);
                    taskManagerImpl.printTask(id);

                    System.out.println("1. Изменить имя задачи");
                    System.out.println("2. Изменить описание задачи");
                    System.out.println("3. Изменить статус задачи");
                    int answer = scanner.nextInt();


                    if (answer == 1) {
                        System.out.println("Какое имя вы хотели бы присвоить");
                        String name = scanner.next();
                        taskManagerImpl.updateName(task, name);
                        System.out.println("Имя задачи успешно изменено на " + name);
                        System.out.println();
                        taskManagerImpl.printTask(id);
                        break;
                    } else if (answer == 2) {
                        System.out.println("Какое описание вы хотели бы присвоить");
                        String description = scanner.next();
                        taskManagerImpl.updateDescription(task, description);
                        System.out.println("Описание задачи успешно изменено на " + description);
                        System.out.println();
                        taskManagerImpl.printTask(id);
                    } else if (answer == 3) {
                        System.out.println("Задача " + task.getName() + " имеет статус " + task.getStatus());
                        taskManagerImpl.changeStatus(id);
                    } else {
                        System.out.println("Введена неверная команда");
                    }
                    break;
                }
                case 7: {
                    System.out.println("Название новой задачи: ");
                    String name = scanner.next();

                    System.out.println("Описание новой задачи: ");
                    String description = scanner.next();

                    Task newTask = new Task(name, description);
                    System.out.println("Задачи создана успешно");
                    System.out.println("На какую задачу заменить, укажите идентификатор");
                    int oldTask = scanner.nextInt();
                    taskManagerImpl.updateTask(oldTask, newTask);
                    System.out.println("Задача успешно заменена");
                    System.out.println();
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
        System.out.println("2. Удалить задачу по идентификатору");
        System.out.println("3. Удалить все задача");
        System.out.println("4. Найти задачу");
        System.out.println("5. Получение списка всех задач");
        System.out.println("6. Изменить имя, описание или статус задачи");
        System.out.println("7. Заменить задачу");
        System.out.println("8. Выход");
        System.out.println();
    }


}
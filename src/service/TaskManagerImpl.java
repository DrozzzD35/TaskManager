package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManagerImpl<T extends Task> implements TaskManager<T> {
    private Map<Integer, T> taskMap;

    public TaskManagerImpl() {
        this.taskMap = new HashMap<>();
    }

    /*
      getTaskById();
        */
    public Task findTask(int id) {
        if (!taskMap.isEmpty()) {
            for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
                if (entry.getKey() == id) {
                    return entry.getValue();
                } else {
                    System.out.println("Задача не найдена. Возможно неверно указан идентификатор");
                    System.out.println();
                    break;
                }
            }
        } else {
            System.out.println("В настоящий момент задач нет");
            System.out.println();
        }
        return null;
    }

    @Override
    public void add(T task) {
        if (task instanceof SubTask) {
            ((SubTask) task).getParent().addChild((SubTask) task);
        }

        taskMap.put(task.getId(), task);
        System.out.println(task.getType() + " " + task.getName() + " добавлен.");
    }

    @Override
    public List<T> getTasks() {
        return new ArrayList<>(taskMap.values());

//        List<T> tasks = new ArrayList<>();
//        for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
//            tasks.add(entry.getValue());
//        }
//        return tasks;
    }

    /*
    findTask();
    */

    @Override
    public T getTaskById(int id) {
//        for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
//            if (entry.getKey() == id) {
//                return entry.getValue();
//            }
//        }
//
//        return null;


        if (!taskMap.containsKey(id)) {
            System.out.println("Задача с таким id не найдена: " + id);
            return null;
        }
        return taskMap.get(id);
    }

    @Override
    public void updateTask(T updateTask) {
        T task = getTaskById(updateTask.getId());

        if (task == null) {
            return;
        }

        task.setName(updateTask.getName());
        task.setDescription(updateTask.getDescription());
        task.setType(updateTask.getType());
        task.setStatus(updateTask.getStatus());

        if (task instanceof SubTask) {
            ((SubTask) task).getParent().updateSubTask((SubTask) task);
        }

    }


    public void removeTaskById(int id) {
        printTask(id);
        Task task = getTaskById(id);
        if (task != null) {
            taskMap.remove(id);
            System.out.println("Задача удалена");
            System.out.println();
        }

    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public List<SubTask> getAllSubtasksByEpicId(int epicId) {
        if (!taskMap.containsKey(epicId)) {
            System.out.println("Большая задача не найдена");
            return new ArrayList<>();
        }

        return ((Epic) taskMap.get(epicId)).getAllChildren();
    }


    public void printAllTasks() {
        if (taskMap.isEmpty()) {
            System.out.println("В настоящий момент задач нет");
            System.out.println();
        } else {
            for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
                System.out.println("Задача: " + entry.getValue().getName());
                System.out.println("Идентификатор: " + entry.getValue().getId());
                System.out.println("Описание: " + entry.getValue().getDescription());
                System.out.println("Статус: " + entry.getValue().getStatus());
                System.out.println("Тип: " + entry.getValue().getType());
                System.out.println();
            }
        }
    }

    public void printTask(int id) {
        Task task = findTask(id);
        if (!(task == null)) {
            System.out.println("Задача: " + task.getName());
            System.out.println("Идентификатор: " + task.getId());
            System.out.println("Описание: " + task.getDescription());
            System.out.println("Статус: " + task.getStatus());
            System.out.println("Тип: " + task.getType());
            System.out.println();
        }
    }

    public void changeStatus(int id) {
        ChangeStatus changeStatus = new ChangeStatus();
        Task task = findTask(id);
        changeStatus.changeStatus(task);
    }


    public void updateName(Task task, String name) {
        task.setName(name);
    }

    public void updateDescription(Task task, String description) {
        task.description = description;
    }

    public void updateTask(int oldTask, Task newTask) {
        Task deletTask = findTask(oldTask);
        int id = deletTask.getId();
        taskMap.remove(id);
        taskMap.put(id, newTask);
    }

    public void printAllEpic() {
        for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
            if (entry.getValue().type == Type.EPIC) {
                System.out.println("Задача: " + entry.getValue().getName());
                System.out.println("Идентификатор: " + entry.getValue().getId());
                System.out.println("Описание: " + entry.getValue().getDescription());
                System.out.println("Статус: " + entry.getValue().getStatus());
                System.out.println("Тип: " + entry.getValue().getType());
                System.out.println();
            }
        }
    }

    public void printAllSubTaskByEpic(int idEpic) {
        if (!(findTask(idEpic) == null)) {
            for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
                if (entry.getValue().type == Type.SUBTASK) {

                }

            }
        }
    }
}




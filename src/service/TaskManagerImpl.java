package service;

import model.*;

import java.util.*;

public class TaskManagerImpl<T extends Task> implements TaskManager<T> {
    private Map<Integer, T> taskMap;

    public TaskManagerImpl() {
        this.taskMap = new HashMap<>();
    }

    public void createTask(String name, String description) {
        T task = new Task(name, description);
        add(task);
    }

    public void createEpicTask(String name, String description) {
        new Epic(name, description);
    }

    public void createSubTaskTask(String name, String description, Epic epicParent) {
        new SubTask(name, description, epicParent);
    }

    public ArrayList<Epic> getAllEpic() {
        for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
            if (entry.getValue() instanceof Epic) {
                return new ArrayList<>((Collection) taskMap.get(Epic.class));
            }
        }
        return null;
    }

    public void printListEpic() {
        List<Epic> list = getAllEpic();
        for (Epic task : list) {
            System.out.println("Название задачи " + task.getName());
            System.out.println("Идентификатор " + task.getId());
        }
    }


    /**
     * getTaskById();
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
    }

    /**
     * findTask();
     */

    @Override
    public T getTaskById(int id) {
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
        Task task = getTaskById(id);
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
        Task task = getTaskById(id);
        changeStatus.changeStatus(task);
    }


    public void updateName(T task, String name) {
        task.setName(name);
    }

    public void updateDescription(T task, String description) {
        task.setDescription(description);
    }


    public void printListTask(List<T> taskList) {
        for (T task : taskList) {
            printTask(task.getId());
        }

    }


}




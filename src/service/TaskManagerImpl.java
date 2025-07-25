package service;

import model.*;

import java.util.*;

public class TaskManagerImpl<T extends Task> implements TaskManager<T> {
    private Map<Integer, T> taskMap;

    public TaskManagerImpl() {
        this.taskMap = new HashMap<>();
    }


    @Override
    public void add(T task) {
        if (task instanceof SubTask) {
            ((SubTask) task).getParent().addChild((SubTask) task);
        }

        taskMap.put(task.getId(), task);
        System.out.println(task.getType() + " " + task.getName() + " добавлена.");
    }

    @Override
    public List<T> getTasks() {
        return new ArrayList<>(taskMap.values());
    }


    @Override
    public T getTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задача с таким идентификатором не найдена: " + id);
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
        System.out.println("Задача обновлена");

    }

    @Override
    public void removeTaskById(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            taskMap.remove(id);
            System.out.println("Задача удалена");
            System.out.println();
            return;
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
            System.out.println("Список всех задач");
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

    public void printEpicTasks() {
        ArrayList<Epic> epics = new ArrayList<>();

        for (Map.Entry<Integer, T> entry : taskMap.entrySet()) {
            if (entry.getValue() instanceof Epic) {
                epics.add((Epic) entry.getValue());
            }
        }

        for (Epic epic : epics) {
            printTask(epic.getId());
        }
    }


}




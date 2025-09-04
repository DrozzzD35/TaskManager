package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.TaskStatus.*;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {
    private Map<Integer, T> taskMap;
    protected HistoryManager<T> history;


    public InMemoryTaskManager() {
        this.history = Managers.getDefaultHistory();
        this.taskMap = new HashMap<>();
    }


    @Override
    public void add(T task) {
        if (task instanceof SubTask) {
            int epicId = ((SubTask) task).getParentId();
            Epic epic = (Epic) taskMap.get(epicId);
            epic.addChild(task.getId());

        }

        taskMap.put(task.getId(), task);
        System.out.println(task.getType() + " " + task.getName() + ", id = " + task.getId() + " добавлена.");
    }

    public HistoryManager<T> getHistory() {
        return history;
    }

    @Override
    public List<T> getTasks() {
        return new ArrayList<>(taskMap.values());
    }


    @Override
    public T getTaskById(int id, boolean withHistory) {

        if (!taskMap.containsKey(id)) {
            System.out.println("Задача с таким идентификатором не найдена: " + id);
            return null;
        }
        T task = taskMap.get(id);

        if (withHistory) {
            history.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(T updateTask, int id) {
        T task = getTaskById(id, false);


        if (task == null) {
            return;
        }

        if (updateTask.getName() != null) {
            task.setName(updateTask.getName());
        }
        if (updateTask.getDescription() != null) {
            task.setDescription(updateTask.getDescription());
        }
        if (updateTask.getType() != null) {
            task.setType(updateTask.getType());
        }
        if (updateTask.getStatus() != null) {
            task.setStatus(updateTask.getStatus());
        }
        if (task instanceof SubTask) {
            updateEpicStatus(id);
        }

        System.out.println("Задача обновлена");

    }

    @Override
    public void removeTaskById(int id) {
        Task task = getTaskById(id, false);
        if (task == null) {
            return;
        }
        if (task instanceof Epic) {
            List<Integer> subTasksIds = ((Epic) task).getAllChildrenIds();
            for (Integer subTaskId : subTasksIds) {
                removeTaskById(subTaskId);
            }

        }
        taskMap.remove(id);
        System.out.println("Задача удалена");
        System.out.println();

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
        Epic epic = (Epic) getTaskById(epicId, false);
        List<Integer> subTasksIds = epic.getAllChildrenIds();
        List<SubTask> subTasks = new ArrayList<>();
        for (Integer subTaskId : subTasksIds) {
            subTasks.add((SubTask) getTaskById(subTaskId, false));
        }


        return subTasks;
    }

    public void addTaskByIdsToHistory(List<Integer> ids){
        for (Integer id:ids){
            T task = getTaskById(id, false);
            history.add(task);
        }
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
        Task task = getTaskById(id, false);
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


    public void updateEpicStatus(Integer epicId) {
        /*
        new
        Done
        new
         */

        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;

        Epic epic = (Epic) getTaskById(epicId, false);
        List<Integer> children = epic.getAllChildrenIds();
        List<SubTask> subTasks = new ArrayList<>();

        for (Integer child : children) {
            subTasks.add((SubTask) getTaskById(child, false));
        }

        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW -> {
                    isNew = true;
                }
                case DONE -> {
                    isDone = true;
                }
                case IN_PROGRESS -> {
                    isInProgress = true;
                }
            }
        }

        /* f && f
        !(isAllDone || isAllNew) = !isAllDone && !isAllNew
        !(isAllDone && isAllNew) = T\F


        !isAllDone && !isAllNew = F
        !(isAllDone && isAllNew) = T\F

        */
        if (isInProgress || (isNew && isDone)) {
            epic.setStatus(IN_PROGRESS);
        } else {
            epic.setStatus(isNew ? NEW : DONE);

        }


    }

}




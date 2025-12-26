package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static model.TaskStatus.*;

public class InMemoryTaskManager<T extends Task> implements TaskManager<T> {
    protected final Map<Integer, T> taskMap;
    protected HistoryManager<T> history;
    //TODO внедрил TreeSet
    private final Set<T> priorityzedTasks = new TreeSet<>
            (Comparator.comparing(Task::getStartTime
                            , Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId));


    public InMemoryTaskManager() {
        this.history = Managers.getDefaultHistory();
        this.taskMap = new HashMap<>();
    }

    public HistoryManager<T> getHistory() {
        return history;
    }

    @Override
    public void add(T task) {
        chekOverlap(task);

        if (task instanceof SubTask subTask) {
            Epic epic = (Epic) getTaskById(subTask.getParentId(), false);

            if (epic == null) {
                System.out.println(("Невозможно создать SubTask, Epic с ID:" + subTask.getParentId() + " не существует\n"));
                return;
            }
            taskMap.put(task.getId(), task);
            priorityzedTasks.add(task);
            epic.addChild(task.getId());
            updateEpicTime(epic.getId());
            updateEpicStatus(epic.getId());
        } else {
            taskMap.put(task.getId(), task);
            priorityzedTasks.add(task);
        }
        System.out.println(task.getType() + " " + task.getName() + ", id = " + task.getId() + " добавлена.");
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
        T oldTask = getTaskById(id, false);

        if (oldTask == null) {
            return;
        }
        priorityzedTasks.remove(oldTask);

        try {
            chekOverlap(updateTask);
        } catch (ValidationException e) {
            priorityzedTasks.add(oldTask);
            System.out.println("Ошибка обновления: " + e.getMessage());
            return;
        }

        if (updateTask.getName() != null) {
            oldTask.setName(updateTask.getName());
        }
        if (updateTask.getDescription() != null) {
            oldTask.setDescription(updateTask.getDescription());
        }
        if (updateTask.getStatus() != null) {
            oldTask.setStatus(updateTask.getStatus());
        }
        if (updateTask.getDuration() != null) {
            oldTask.setDuration(updateTask.getDuration());
        }
        if (updateTask.getStartTime() != null) {
            oldTask.setStartTime(updateTask.getStartTime());
        }
        priorityzedTasks.add(oldTask);

        if (oldTask instanceof SubTask subTask) {
            Epic epic = (Epic) getTaskById(subTask.getParentId(), false);
            if (epic != null) {
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }

        System.out.println("Задача обновлена: " + oldTask);
    }

    @Override
    public void updateEpicStatus(Integer epicId) {
        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;

        Epic epic = (Epic) getTaskById(epicId, false);

        List<Integer> children = epic.getChildrenIds();
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

        if (isInProgress || (isNew && isDone)) {
            epic.setStatus(IN_PROGRESS);
        } else {
            epic.setStatus(isNew || children.isEmpty() ? NEW : DONE);
        }

    }

    @Override
    public void removeTaskById(int id) {
        Task task = getTaskById(id, false);
        if (task == null) {
            return;
        }
        if (task instanceof Epic) {
            List<Integer> subTasksIds = ((Epic) task).getChildrenIds();
            for (Integer subTaskId : subTasksIds) {
                removeTaskById(subTaskId);
            }

        }
        priorityzedTasks.remove(taskMap.get(id));
        taskMap.remove(id);
        System.out.println("Задача удалена");
        System.out.println();

    }

    @Override
    public void removeAllTasks() {
        priorityzedTasks.clear();
        taskMap.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeTasks(Type type) {
        switch (type) {
            case SUBTASK -> {
                for (T task : taskMap.values()) {
                    if (task instanceof Epic epic) {
                        epic.removeChildren();
                        updateEpicStatus(task.getId());
                        updateEpicTime(epic.getId());
                    }
                }
                priorityzedTasks.removeIf(task -> task instanceof SubTask);
                taskMap.values().removeIf(task -> task instanceof SubTask);
            }
            case EPIC -> {
                priorityzedTasks.removeIf(task -> task instanceof Epic || task instanceof SubTask);
                taskMap.values().removeIf(task -> task instanceof Epic || task instanceof SubTask);
            }
            case TASK -> {
                priorityzedTasks.removeIf(task -> task.getType() == Type.TASK);
                taskMap.values().removeIf(task -> task.getType() == Type.TASK);
            }
            default -> System.out.println("неизвестный тип");
        }

    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        if (!taskMap.containsKey(epicId)) {
            System.out.println("Большая задача не найдена");
            return new ArrayList<>();
        }
        Epic epic = (Epic) getTaskById(epicId, false);
        List<Integer> subTasksIds = epic.getChildrenIds();
        List<SubTask> subTasks = new ArrayList<>();
        for (Integer subTaskId : subTasksIds) {
            subTasks.add((SubTask) getTaskById(subTaskId, false));
        }

        return subTasks;
    }

    @Override
    public List<T> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<T> getTasks(Type type) {
        return taskMap.values().stream().filter(t -> t.getType() == type).toList();
    }

    public Set<T> getPriorityzedTasks() {
        return priorityzedTasks;
    }

    public void updateEpicTime(int id) {
        Epic epic = (Epic) getTaskById(id, false);
        if (epic == null) {
            return;
        }
        List<Integer> subTasksIds = epic.getChildrenIds();

        if (subTasksIds.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        Duration duration = Duration.ZERO;

        for (Integer idSubTask : subTasksIds) {
            SubTask subTask = (SubTask) taskMap.get(idSubTask);
            if (subTask == null || subTask.getStartTime() == null) continue;

            duration = duration.plus(subTask.getDuration());

            if (start == null || subTask.getStartTime().isBefore(start)) {
                start = subTask.getStartTime();
            }

            if (end == null || subTask.getEndTime().isAfter(end)) {
                end = subTask.getEndTime();
            }
        }

        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    public void chekOverlap(T task) {
        if (task.getStartTime() == null) return;

        boolean overlap = getPriorityzedTasks()
                .stream().filter(t -> t.getStartTime() != null
                        && !t.getId().equals
                        (task.getId())).anyMatch(existTask -> {
                    return task.getStartTime()
                            .isBefore(existTask.getEndTime())
                            && existTask.getStartTime().isBefore
                            (task.getEndTime());
                });

        if (overlap) {
            throw new ValidationException("Ошибка! Несколько задач не могут быть запущены одновременно");
        }
    }

    public void addTaskByIdsToHistory(Integer[] ids) {
        for (Integer id : ids) {
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

    public void printTaskById(int id) {
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
}




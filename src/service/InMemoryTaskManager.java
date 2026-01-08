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
    private final Set<T> prioritizedTasks = new TreeSet<>
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
            prioritizedTasks.add(task);
            epic.addChild(task.getId());
            updateEpicTime(epic.getId());
            updateEpicStatus(epic.getId());
        } else {
            taskMap.put(task.getId(), task);
            if (!(task instanceof Epic)) {
                prioritizedTasks.add(task);
            }
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
    public void updateTask(T newTask, int id) {
        T taskInMap = getTaskById(id, false);

        if (taskInMap == null) {
            return;
        }
        prioritizedTasks.remove(taskInMap);

        try {
            chekOverlap(newTask);

            if (newTask.getName() != null) {
                taskInMap.setName(newTask.getName());
            }
            if (newTask.getDescription() != null) {
                taskInMap.setDescription(newTask.getDescription());
            }
            if (newTask.getStatus() != null) {
                taskInMap.setStatus(newTask.getStatus());
            }
            if (newTask.getDuration() != null) {
                taskInMap.setDuration(newTask.getDuration());
            }
            if (newTask.getStartTime() != null) {
                taskInMap.setStartTime(newTask.getStartTime());
            }
            if (taskInMap.getStartTime() != null && !(taskInMap instanceof Epic)) {
                prioritizedTasks.add(taskInMap);
            }

        } catch (ValidationException e) {
            prioritizedTasks.add(taskInMap);
            System.out.println("Ошибка обновления: " + e.getMessage());
            return;
        }

        if (taskInMap instanceof SubTask subTask) {
            Epic epic = (Epic) getTaskById(subTask.getParentId(), false);
            if (epic != null) {
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }

        System.out.println("Задача обновлена: " + taskInMap);
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
            if (subTask == null) {
                continue;
            }

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

        if (task instanceof SubTask subTask) {
            int epicId = subTask.getParentId();
            Epic epic = (Epic) getTaskById(epicId, false);
            epic.removeChild(id);
        }

        if (task instanceof Epic) {
            List<Integer> subTasksIds = ((Epic) task).getChildrenIds();
            for (Integer subTaskId : subTasksIds) {
                removeTaskById(subTaskId);
            }

        }
        prioritizedTasks.remove(taskMap.get(id));
        history.removeTask((T) task);
        taskMap.remove(id);
        System.out.println("Задача удалена, id:" + id + '\n');
    }

    @Override
    public void removeAllTasks() {
        prioritizedTasks.clear();
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
                prioritizedTasks.removeIf(task -> task instanceof SubTask);
                taskMap.values().removeIf(task -> task instanceof SubTask);
            }
            case EPIC -> {
                prioritizedTasks.removeIf(task -> task instanceof Epic || task instanceof SubTask);
                taskMap.values().removeIf(task -> task instanceof Epic || task instanceof SubTask);
            }
            case TASK -> {
                prioritizedTasks.removeIf(task -> task.getType() == Type.TASK);
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

    @Override
    public Set<T> getPrioritizedTasks() {
        return prioritizedTasks;
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
        if (task.getStartTime() == null
                || task.getEndTime() == null
                || task instanceof Epic) return;

        boolean overlap = getPrioritizedTasks()
                .stream().filter(t -> t.getStartTime() != null
                        && !t.getId().equals(task.getId())
                        && t.getEndTime() != null
                        && !(t instanceof Epic))
                .anyMatch(existTask -> {
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

}




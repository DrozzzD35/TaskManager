package service;

import model.SubTask;
import model.Task;
import model.Type;

import java.util.List;

public interface TaskManager<T extends Task> {

    void add(T task);

    List<T> getAllTasks();

    T getTaskById(int id, boolean withHistory);

    void updateTask(T task, int id);

    void updateEpicStatus(Integer epicId);

    void removeTaskById(int id);

    void removeAllTasks();

    void removeTasks(Type type);

    List<SubTask> getSubtasksByEpicId(int epicId);

    HistoryManager<T> getHistory();

    void addTaskByIdsToHistory(Integer[] ids);

    List<T> getTasks(Type type);

}

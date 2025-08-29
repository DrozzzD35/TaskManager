package service;

import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager<T extends Task> {

    void add(T task);

    List<T> getTasks();

    T getTaskById(int id);

    void updateTask(T task, int id);

    void removeTaskById(int id);

    void removeAllTasks();

    List<SubTask> getAllSubtasksByEpicId(int epicId);

    HistoryManager<T> getHistory();
}

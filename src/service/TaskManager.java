package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager<T extends Task> {

    void add(T task);

    List<T> getTasks();

    T getTaskById(int id, boolean withHistory);

    void updateTask(T task, int id);

    void removeTaskById(int id);

    void removeAllTasks();

    List<SubTask> getSubtasksByEpicId(int epicId);

    HistoryManager<T> getHistory();

    List<Epic> getEpics();

}

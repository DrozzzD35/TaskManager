package service;

import model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    private LinkedList<T> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    public InMemoryHistoryManager(LinkedList<T> history) {
        this.history = history;
    }

    @Override
    public void add(T task) {
        if (history.size() > 9) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public LinkedList<T> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "History{" + "history=" + '\n' + history + '}' + '\n';
    }

    public void remove(T task){
        history.remove(task);
    }
}

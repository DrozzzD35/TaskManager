package model;

import java.util.LinkedList;

public class History<T extends Task> {
    private LinkedList<T> history;

    public History(LinkedList<T> history) {
        this.history = history;
    }

    public void add(T task) {
        if (history.size() > 9) {
            history.removeFirst();
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public String toString() {
        return "History{" + "history=" + '\n' + history + '}' + '\n';
    }
}

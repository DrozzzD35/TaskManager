package model;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(String name, String description, Epic parent) {
        super(name, description); // new Task(name, description);

        this.type = Type.SUBTASK;
        this.parent = parent;

    }

    public Epic getParent() {
        return parent;
    }
}

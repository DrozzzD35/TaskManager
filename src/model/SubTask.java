package model;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(String name, String description, Epic parent) {
        super(name, description); // new Task(name, description);

        this.taskStatus = taskStatus.NEW;
        this.type = Type.SUBTASK;
        this.parent = parent;

    }

    public Epic getParent() {
        return parent;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "parent=" + parent +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                ", type=" + type +
                '}';
    }
}

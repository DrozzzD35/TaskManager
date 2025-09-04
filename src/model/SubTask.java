package model;

public class SubTask extends Task {
    private int parentId;

    public SubTask(String name, String description,int parentId) {
        super(name, description); // new Task(name, description);

        this.taskStatus = taskStatus.NEW;
        this.type = Type.SUBTASK;
        this.parentId = parentId;

    }

    public SubTask(Integer id, Type type, String name
            , TaskStatus taskStatus, String description, int parentId) {
        super(id, type, name, taskStatus, description);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "parent=" + parentId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                ", type=" + type +
                '}';
    }
}

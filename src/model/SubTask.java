package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final int parentId;

    public SubTask(String name, String description
            , LocalDateTime startTime
            , Duration duration, int parentId) {

        super(name, description, startTime, duration);

        this.taskStatus = TaskStatus.NEW;
        this.type = Type.SUBTASK;
        this.parentId = parentId;

    }

    public SubTask(Integer id, Type type, String name
            , TaskStatus taskStatus, String description
            , LocalDateTime startTime, Duration duration, int parentId) {

        super(id, type, name, taskStatus, description, startTime, duration);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return super.toString() + "parentId = " + parentId + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SubTask subTask)) return false;
        if (!super.equals(subTask)) return false;

        return Objects.equals(parentId, subTask.getParentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentId);
    }
}

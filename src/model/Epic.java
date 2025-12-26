package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);

        this.taskStatus = TaskStatus.NEW;
        this.type = Type.EPIC;
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(Integer id
            , Type type, String name
            , TaskStatus taskStatus, String description
            , LocalDateTime startTime, Duration duration) {

        super(name, description);

        this.id = id;
        this.taskStatus = taskStatus;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;

        this.subTasksIds = new ArrayList<>();
    }

    public void removeChildren() {
        subTasksIds.clear();
    }

    public List<Integer> getChildrenIds() {
        return subTasksIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addChild(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    @Override
    public String toString() {
        return super.toString()
                + "endTime=" + endTime + '\n'
                + "subTasksIds=" + subTasksIds + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Epic epic)) return false;
        if (!(super.equals(epic))) return false;

        return Objects.equals(subTasksIds, epic.getChildrenIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }
}
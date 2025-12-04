package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);

        this.type = Type.EPIC;
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(Integer id, Type type, String name, TaskStatus taskStatus, String description) {
        super(id, type, name, taskStatus, description);
        this.subTasksIds = new ArrayList<>();

    }

    public void removeAllChildren(){
        subTasksIds.clear();
    }

    public List<Integer> getAllChildrenIds() {
        return subTasksIds;
    }

    public void addChild(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    @Override
    public String toString() {
        return '\n' + "Epic{" +
                "subTasksIds=" + subTasksIds + '\n'
                + "id=" + id + '\n'
                + "name='" + name + '\'' + '\n'
                + "description='" + description + '\'' + '\n'
                + "taskStatus=" + taskStatus + '\n'
                + "type=" + type
                + '}' + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Epic epic)) return false;
        if (!(super.equals(epic))) return false;

        return Objects.equals(subTasksIds, epic.getAllChildrenIds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }
}
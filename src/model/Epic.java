package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private List<Integer> subTasksIds;

    public Epic(String nameEpic, String descriptionEpic) {
        super(nameEpic, descriptionEpic);

        this.type = Type.EPIC;
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(Integer id, Type type, String name, TaskStatus taskStatus, String description) {
        super(id, type, name, taskStatus, description);
    }


    public List<Integer> getAllChildrenIds() {
        return subTasksIds;
    }

    public void addChild(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksIds=" + subTasksIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", type=" + type +
                '}';
    }
}
package model;

import java.util.ArrayList;
import java.util.List;

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
        return '\n' + "Epic{" +
                "subTasksIds=" + subTasksIds + '\n'
                + "id=" + id + '\n'
                + "name='" + name + '\'' + '\n'
                + "description='" + description + '\'' + '\n'
                + "taskStatus=" + taskStatus + '\n'
                + "type=" + type
                + '}' + '\n';
    }
}
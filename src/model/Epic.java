package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private Map<Integer, SubTask> children;

    public Epic(String nameEpic, String descriptionEpic) {
        super(nameEpic, descriptionEpic);
        this.type = Type.EPIC;
        this.children = new HashMap<>();
    }

    public void addChild(SubTask subTask) {
        children.put(subTask.getId(), subTask);
    }

    public List<SubTask> getAllChildren() {
//         return new ArrayList<>(children.values());

        List<SubTask> subTasks = new ArrayList<>();
        for (Map.Entry<Integer, SubTask> entry : children.entrySet()) {
            subTasks.add(entry.getValue());
        }
        return subTasks;

    }

    public void removeSubTask(int subtaskId) {
        children.remove(subtaskId);
    }

    public void updateSubTask(SubTask subTask) {
        if (children.containsKey(subTask.getId())) {
            children.put(subTask.getId(), subTask);
        }
    }
}
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task {
    private Map<Integer, SubTask> children;

    public Epic(String nameEpic, String descriptionEpic) {
        super(nameEpic, descriptionEpic);

        this.status = Status.NEW;
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
            updateStatus(subTask);
        }
    }

    public void updateStatus(SubTask subTask) {
        subTask.setStatus(Status.NEW);
    }

    public void updateEpicStatus(Epic epic) {
        if (!epic.children.isEmpty()) {
            epic.setStatus(Status.NEW);
        }

        for (Map.Entry<Integer, SubTask> entry : epic.children.entrySet()) {
            if (entry.getValue().getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (!(entry.getValue().getStatus().equals(Status.NEW)
                    && entry.getValue().getStatus().equals(Status.IN_PROGRESS))) {
                epic.setStatus(Status.DONE);
            }
        }
    }

}
package model;

import javax.swing.plaf.LabelUI;
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
        return new ArrayList<>(children.values());
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
        List<SubTask> subTasksList = new ArrayList<>(epic.children.values());

        for (SubTask subTask : subTasksList) {
            if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            } else if (!subTask.getStatus().equals(Status.NEW)) {
                epic.setStatus(Status.DONE);
            }
        }
    }

}
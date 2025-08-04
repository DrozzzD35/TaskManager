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
            updateStatus();
        }
    }

    public void updateStatus() {
        /*
        new
        Done
        new
         */

        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;

        for (SubTask subTask : children.values()) {
            switch (subTask.getStatus()) {
                case NEW -> {
                    isNew = true;
                }
                case DONE -> {
                    isDone = true;
                }
                case IN_PROGRESS -> {
                    isInProgress = true;
                }
            }
        }

        /* f && f
        !(isAllDone || isAllNew) = !isAllDone && !isAllNew
        !(isAllDone && isAllNew) = T\F


        !isAllDone && !isAllNew = F
        !(isAllDone && isAllNew) = T\F

        */
        if (isInProgress || (isNew && isDone)) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(isNew ? Status.NEW : Status.DONE);

        }


    }

    @Override
    public void setStatus(Status status) {
        updateStatus();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "children=" + children +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
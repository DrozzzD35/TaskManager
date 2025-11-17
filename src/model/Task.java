package model;


import utils.Identity;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;
    protected Type type;

    public Task(String name, String description) {
        this.id = Identity.INSTANCE.generateId();
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus.NEW;
        this.type = Type.TASK;
    }

    public Task(Integer id, Type type, String name, TaskStatus taskStatus, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public Type getType() {
        return type;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return '\n' + "Task{" + "id=" + id + '\n' + "name=" + name + '\n' + "description=" + description + '\n' + "status=" + taskStatus + '\n' + "type=" + type + '}' + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task task)) return false;

        return Objects.equals(id, task.id) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(taskStatus, task.taskStatus) &&
                Objects.equals(type, task.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus, type);
    }
}

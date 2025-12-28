package model;


import utils.GsonFactory;
import utils.Identity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;
    protected Type type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = Identity.INSTANCE.generateId();
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.taskStatus = TaskStatus.NEW;
        this.type = Type.TASK;
    }

    public Task(Integer id, Type type, String name, TaskStatus taskStatus, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.taskStatus = taskStatus;
        this.type = type;
    }

    public Task(String name, String description) {
        this.id = Identity.INSTANCE.generateId();
        this.name = name;
        this.description = description;
        this.startTime = null;
        this.duration = Duration.ZERO;
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

    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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
        return "\n" + type + ": id = " + id + '\n'
                + "name = " + name + '\n' + "description = " + description + '\n'
                + "status = " + taskStatus + '\n' + "type = " + type + '\n'
                + "duration = " + duration.toMinutes() + '\n'
                + "startTime = " + startTime.format(GsonFactory.DATE_TIME_FORMATTER)
                + '\n';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task task)) return false;

        return Objects.equals(id, task.id) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(taskStatus, task.taskStatus) &&
                Objects.equals(type, task.type) &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus, type, duration, startTime);
    }
}

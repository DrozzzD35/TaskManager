package model;


import utils.Identity;

import java.util.List;
import java.util.Map;

public class Task {
    protected Integer id;

    protected String name;

    protected String description;
    protected Status status;
    protected Type type;

    public Task(String name, String description) {
        this.id = Identity.INSTANCE.generateId();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
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

    public Status getStatus() {
        return status;
    }

    public Type getType() {
        return type;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


}

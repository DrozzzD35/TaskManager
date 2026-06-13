package model;

public enum Type {
    TASK("Task"),
    EPIC("Epic"),
    SUBTASK("Subtask");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}

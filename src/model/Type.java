package model;

public enum Type {
    TASK("Задача"),
    EPIC("Большая задача"),
    SUBTASK("Подзадача");

    private String type;

    Type(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}

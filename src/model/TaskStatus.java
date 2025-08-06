package model;

public enum TaskStatus {
    NEW("Новая задача"),
    IN_PROGRESS("Над задачей ведётся работа"),
    DONE("Задача завершена");

    private String status;

    TaskStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}

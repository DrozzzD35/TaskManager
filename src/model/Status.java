package model;

public enum Status {
    NEW("Новая задача"),
    IN_PROGRESS("Над задачей ведётся работа"),
    DONE("Задача завершена");

    private String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}

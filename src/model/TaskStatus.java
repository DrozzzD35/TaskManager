package model;

public enum TaskStatus {
    NEW("New"),
    IN_PROGRESS("In progress"),
    DONE("Done");

    private String status;

    TaskStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}

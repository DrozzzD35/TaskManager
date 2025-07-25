package utils;


public enum Identity {
    INSTANCE;
    private int identifier = 1;

    public int generateId() {
        return identifier++;
    }
}
package utils;


public enum Identity {
    INSTANCE;
    private int identifier = 0;

    public int generateId() {
        return identifier++;
    }
}
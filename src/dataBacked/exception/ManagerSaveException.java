package dataBacked.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super("Ошибка при сохранении данных. " + message);
    }
}

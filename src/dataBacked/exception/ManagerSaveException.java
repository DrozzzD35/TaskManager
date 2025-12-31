package dataBacked.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String massage) {
        super("Ошибка при сохранении данных. " + massage);
    }
}

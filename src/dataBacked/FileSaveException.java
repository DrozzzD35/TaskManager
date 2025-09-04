package dataBacked;

public class FileSaveException extends RuntimeException {
    public FileSaveException(String message) {
        super("Ошибка при  сохранение файла " + message);
    }
}

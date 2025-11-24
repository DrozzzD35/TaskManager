package server;

public class NotFoundException extends RuntimeException {
    public NotFoundException(int id) {
        super("Задачи с идентификатором " + id +" не существует");
    }
}

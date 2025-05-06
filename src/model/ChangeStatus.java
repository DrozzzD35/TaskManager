package model;

public class ChangeStatus {

    public void changeStatus(Task task) {
        if (task.getStatus() == Status.NEW) {
            System.out.println("Началась работа над задачей " + task.getName());
            task.setStatus(Status.IN_PROGRESS);
            System.out.println("Статус задачи изменён на: " + Status.IN_PROGRESS);
            System.out.println();
        } else if (task.getStatus() == Status.IN_PROGRESS) {
            task.setStatus(Status.DONE);
            System.out.println("Задача " + task.getName() + " завершена");
            System.out.println("Статус задачи изменён на: " + Status.DONE);
            System.out.println();
        } else {
            System.out.println("Произошла ошибка. Проверьте статус задачи");
            System.out.println();
        }

    }


}

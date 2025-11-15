//package model;
//
//public class ChangeStatus {
//
//    public void changeStatus(Task task) {
//        if (task.getStatus().equals(TaskStatus.NEW)) {
//            System.out.println("Началась работа над задачей " + task.getName());
//            task.setStatus(TaskStatus.IN_PROGRESS);
//            System.out.println("Статус задачи изменён на: " + TaskStatus.IN_PROGRESS);
//            System.out.println();
//        } else if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
//            task.setStatus(TaskStatus.DONE);
//            System.out.println("Задача " + task.getName() + " завершена");
//            System.out.println("Статус задачи изменён на: " + TaskStatus.DONE);
//            System.out.println();
//        } else {
//            System.out.println("Произошла ошибка. Проверьте статус задачи");
//            System.out.println();
//        }
//
//    }
//
//
//}

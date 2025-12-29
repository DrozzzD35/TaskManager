package server;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.HistoryManager;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler<T extends Task> extends BaseHandler<T> {

    public HistoryHandler(TaskManager<T> taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode;
        HistoryManager<T> historyManager = taskManager.getHistory();

        try {
            if (method.equals("GET")) {
                List<T> historyList = historyManager.getHistory();
                response = gson.toJson(historyList);
                statusCode = 200;

            } else if (method.equals("DELETE")) {
                historyManager.removeHistory();
                response = gson.toJson("История полностью удалена");
                statusCode = 200;

            } else {
                response = gson.toJson("Не удалось распознать запрос");
                statusCode = 501;
            }
        } catch (Exception e) {
            response = gson.toJson("Возникла ошибка сервера");
            statusCode = 500;
        }

        sendResponse(exchange, statusCode, response);

    }
}

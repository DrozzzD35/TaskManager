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

        try {
            if (method.equals("GET")) {
                HistoryManager<T> historyManager = taskManager.getHistory();
                List<T> historyList = historyManager.getHistory();

                if (historyList.isEmpty()) {
                    response = gson.toJson("Списка истории пуст");
                    statusCode = 404;
                } else {
                    response = gson.toJson(historyList);
                    statusCode = 200;
                }

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

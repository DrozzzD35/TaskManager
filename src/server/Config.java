package server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private int port;
    private String url;
    private String tasks;
    private String task;
    private String epic;
    private String history;
    private String subTask;

    public Config() {
        Properties properties = new Properties();

        try (InputStream is =
                     getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new NotFoundException("Файл application.properties не найден");
            }

            properties.load(is);

            port = Integer.parseInt(properties.getProperty("server.port"));
            url = properties.getProperty("server.url");
            tasks = properties.getProperty("server.tasks");
            task = properties.getProperty("server.task");
            subTask = properties.getProperty("server.subTask");
            epic = properties.getProperty("server.epic");
            history = properties.getProperty("server.history");

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении конфига" + e);
        } catch (NullPointerException e) {
            throw new NotFoundException("Указан некорректный формат порта" + e);
        }
    }


    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }

    public String getTasks() {
        return tasks;
    }

    public String getTask() {
        return task;
    }

    public String getEpic() {
        return epic;
    }

    public String getHistory() {
        return history;
    }

    public String getSubTask() {
        return subTask;
    }
}

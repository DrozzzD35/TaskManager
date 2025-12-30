package server;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import model.Type;
import service.FileBackedTasksManager;
import utils.GsonFactory;

import java.nio.file.Path;
import java.util.List;

public class HttpTaskManager<T extends Task> extends FileBackedTasksManager<T> {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = GsonFactory.createGson();

    public HttpTaskManager(String url) {
        super(Path.of("src/dataBacked/Ignored.CSV"));

        this.kvTaskClient = new KVTaskClient(url);
        loadFromServer();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(getTasks(Type.TASK));
        String jsonEpics = gson.toJson(getTasks(Type.EPIC));
        String jsonSubTasks = gson.toJson(getTasks(Type.SUBTASK));
        String jsonHistory = gson.toJson(getHistory());

        kvTaskClient.put("Tasks", jsonTasks);
        kvTaskClient.put("Epics", jsonEpics);
        kvTaskClient.put("SubTasks", jsonSubTasks);
        kvTaskClient.put("History", jsonHistory);
    }

    public void loadFromServer() {
        loadTasks("Tasks");
        loadTasks("Epics");
        loadTasks("SubTasks");

        String json = kvTaskClient.load("History");
        if (json == null || json.isEmpty() || json.equals("[]")) return;
        List<T> tasks = gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());

        for (T task : tasks) {
            history.add(getTaskById(task.getId(), false));
        }

        for (Task epic : getTasks(Type.EPIC)) {
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        }

    }

    //TODO прибегнул к нехорошему фокусу
    @SuppressWarnings("unchecked")
    private void loadTasks(String key) {
        String json = kvTaskClient.load(key);
        if (json == null || json.isEmpty() || json.equals("[]")) return;

        switch (key) {
            case "Tasks" -> {
                List<Task> tasks = gson.fromJson(json, new TypeToken<List<Task>>() {
                }.getType());
                for (Task task : tasks) {
                    addWithoutSave((T) task);
                }
            }
            case "Epics" -> {
                List<Epic> epics = gson.fromJson(json, new TypeToken<List<Epic>>() {
                }.getType());
                for (Epic epic : epics) {
                    addWithoutSave((T) epic);
                }
            }
            case "SubTasks" -> {
                List<SubTask> subTasks = gson.fromJson(json, new TypeToken<List<SubTask>>() {
                }.getType());
                for (SubTask subTask : subTasks) {
                    addWithoutSave((T) subTask);
                }
            }
        }
    }
}

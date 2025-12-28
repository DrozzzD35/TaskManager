package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.gsonAdapter.DurationAdapter;
import server.gsonAdapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GsonFactory {
       public static DateTimeFormatter DATE_TIME_FORMATTER
               = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private GsonFactory() {
        throw new UnsupportedOperationException("Утилитарный класс");
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

}

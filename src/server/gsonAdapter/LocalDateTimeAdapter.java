package server.gsonAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import utils.GsonFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
            return;
        }

        String formattedDateTime = localDateTime.format(GsonFactory.DATE_TIME_FORMATTER);
        jsonWriter.value(formattedDateTime);
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (jsonReader.peek() == JsonToken.NULL){
            jsonReader.nextNull();
            return null;
        }
        if (token == JsonToken.STRING){
            return LocalDateTime.parse(jsonReader.nextString(), GsonFactory.DATE_TIME_FORMATTER);
        }
        return null;
    }
}

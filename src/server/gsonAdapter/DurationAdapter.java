package server.gsonAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
            return;
        }

        long longDuration = duration.toMinutes();
        jsonWriter.value(longDuration);
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        JsonToken token = jsonReader.peek();

        if (token == JsonToken.STRING){
            return Duration.parse(jsonReader.nextString());
        }
        if (token == JsonToken.NUMBER) {
            return Duration.ofMinutes(jsonReader.nextLong());
        }
        return null;
    }
}

package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter writer, Duration duration) throws IOException {
        if (Objects.isNull(duration)) {
            writer.nullValue();
        } else {
            writer.value(duration.toString());
        }
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return Duration.parse(reader.nextString());
    }

}

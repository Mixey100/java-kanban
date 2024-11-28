package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter writer, LocalDateTime dateTime) throws IOException {
        if (Objects.isNull(dateTime)) {
            writer.value("null");
        } else {
            writer.value(dateTime.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(reader.nextString(), formatter);
        } catch (DateTimeParseException exception) {
            dateTime = null;
        }
        return dateTime;
    }
}

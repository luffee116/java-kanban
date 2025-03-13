package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import util.DTF;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) { // Проверяем на null перед записью
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(DTF.getDTF()));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return LocalDateTime.of(1, 1, 1, 0, 0);
        }
        return LocalDateTime.parse(jsonReader.nextString(), DTF.getDTF());
    }
}

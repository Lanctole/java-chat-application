package lanctole.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonService {
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T deserialize(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public String serialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return "{}";
        }
    }
}

package lanctole.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = Message.MessageBuilder.class)
public class Message {
    @JsonProperty("sender")
    String sender;

    @JsonProperty("content")
    String content;

    @JsonProperty("type")
    MessageType type;

    @JsonProperty("datetime")
    String datetime;
}

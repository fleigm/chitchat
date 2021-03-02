package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebSocketMessage {

  @JsonProperty("type")
  private String type;

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type"
  )
  @JsonSubTypes({
      @JsonSubTypes.Type(value = BroadcastMessagePayload.class, name = BroadcastMessagePayload.TYPE),
      @JsonSubTypes.Type(value = SendMessagePayload.class, name = SendMessagePayload.TYPE),
  })
  private Payload payload;

  public WebSocketMessage(Payload payload) {
    this.payload = payload;
    this.type = payload.getType();
  }
}

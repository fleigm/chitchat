package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.fleigm.chitchat.chat.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BroadcastMessagePayload implements Payload {
  public static final String TYPE = "message";

  @JsonUnwrapped
  private Message message;

  @Override
  public UUID getChat() {
    return message.getChat();
  }

  @Override
  public String getType() {
    return TYPE;
  }
}

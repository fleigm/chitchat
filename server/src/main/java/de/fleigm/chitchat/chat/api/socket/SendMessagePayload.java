package de.fleigm.chitchat.chat.api.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessagePayload implements Payload {
  public static final String TYPE = "send_message";

  private UUID chat;
  private String text;

  @Override
  public String getType() {
    return TYPE;
  }


}

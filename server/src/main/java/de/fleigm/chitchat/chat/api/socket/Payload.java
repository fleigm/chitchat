package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public interface Payload {

  UUID getChat();

  @JsonIgnore
  String getType();
}

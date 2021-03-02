package de.fleigm.chitchat.chat.api.socket;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Base interface for all types of web socket messages.
 */
public interface Payload {

  UUID getChat();

  @JsonIgnore
  String getType();
}

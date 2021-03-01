package de.fleigm.chitchat.chat.api.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
  private UUID sender;
  private UUID chat;
  private String text;
  private LocalDateTime sentAt;
}

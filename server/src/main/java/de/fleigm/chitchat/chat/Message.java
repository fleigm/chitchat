package de.fleigm.chitchat.chat;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message extends PanacheEntity {

  private String text;
  private LocalDateTime sentAt;
  private UUID sender;
  private UUID chat;
}

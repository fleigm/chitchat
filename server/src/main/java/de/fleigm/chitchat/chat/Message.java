package de.fleigm.chitchat.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fleigm.chitchat.users.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Message extends PanacheEntity {

  private String text;
  private LocalDateTime sentAt;

  @ManyToOne
  private User sender;

  @ManyToOne
  @JsonIgnore
  private Chat chat;
}

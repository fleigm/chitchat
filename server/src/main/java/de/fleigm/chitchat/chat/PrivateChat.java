package de.fleigm.chitchat.chat;

import de.fleigm.chitchat.users.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("private")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChat extends Chat {

  @ManyToOne
  private User participantA;

  @ManyToOne
  private User participantB;

  @Override
  public boolean canSendMessage(User user) {
    return participantA.equals(user) || participantB.equals(user);
  }

  public static PanacheQuery<PrivateChat> findByParticipant(UUID id) {
    return PrivateChat.find("participantA.id = ?1 or participantB.id = ?1", id);
  }

  public static PrivateChat findByParticipants(UUID a, UUID b) {
    return PrivateChat.find("participantA.id in ?1 and participantB.id in ?1", List.of(a, b)).firstResult();
  }
}

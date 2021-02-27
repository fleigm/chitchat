package de.fleigm.chitchat.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.fleigm.chitchat.users.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GroupMember extends PanacheEntity {

  @ManyToOne
  private User user;

  private Role role;

  @ManyToOne
  @JsonIgnore
  private GroupChat chat;

  public boolean is(User user) {
    return this.user.equals(user);
  }


  public enum Role {
    USER,
    ADMIN;
  }
}

package de.fleigm.chitchat.users;

import de.fleigm.chitchat.EntityNotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
public class User extends PanacheEntityBase {
  @Id
  private UUID id;
  private String username;

  protected User() {}

  public User(UUID id, String username) {
    this.id = id;
    this.username = username;
  }

  public static User findByIdOrFail(Object id) {
    User user = User.findById(id);

    if (user == null) {
      throw EntityNotFoundException.of(id, User.class);
    }

    return user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

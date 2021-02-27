package de.fleigm.realtalk.users;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class User extends PanacheEntityBase {
  @Id
  public UUID id;
  public String username;

  protected User() {}

  public User(UUID id, String username) {
    this.id = id;
    this.username = username;
  }
}

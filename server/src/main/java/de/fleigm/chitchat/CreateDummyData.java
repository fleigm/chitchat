package de.fleigm.chitchat;

import com.github.javafaker.Faker;
import de.fleigm.chitchat.users.User;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.UUID;

@Singleton
@Startup
public class CreateDummyData {

  @PostConstruct
  @Transactional
  public void createDummyData() {
    Faker faker = new Faker();

    for (int i = 0; i < 20; i++) {
      new User(UUID.randomUUID(), faker.name().username()).persist();
    }
  }
}

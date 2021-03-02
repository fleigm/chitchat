package de.fleigm.chitchat.chat;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class GroupChatTest {

  @Inject
  Transaction transaction;

  @Test
  void find_by_member() {
    User user = transaction.run((Supplier<User>) Factory::createUser);
    transaction.run(() -> Factory.createGroupChats(user, 5));
    transaction.run(() -> Factory.createGroupChats(Factory.createUser(), 10));

    assertEquals(5, GroupChat.findByMember(user.getId()).count());
  }

}
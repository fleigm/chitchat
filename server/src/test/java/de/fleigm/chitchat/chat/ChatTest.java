package de.fleigm.chitchat.chat;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ChatTest {

  @Inject
  Transaction transaction;

  @Test
  @Transactional
  void can_create_chats() {
    User userA = new User(UUID.randomUUID(), "A");
    User userB = new User(UUID.randomUUID(), "B");

    userA.persist();
    userB.persist();

    Chat privateChat = new PrivateChat(userA, userB);
    privateChat.persist();

    Chat dbChat = Chat.findById(privateChat.getId());

    assertNotNull(dbChat);
    assertEquals(PrivateChat.class, dbChat.getClass());
  }


  @Test
  void get_sorted_chat_from_user() {
    User user = transaction.run(Factory::createUser);
    List<PrivateChat> privateChats = transaction.run(() -> Factory.createPrivateChats(user, 10));
    List<GroupChat> groupChats = transaction.run(() -> Factory.createGroupChats(user, 5));

    List<Chat> chats = Chat.findByUser(user);

    assertEquals(15, chats.size());
  }

  @Test
  void get_sorted_messages() {
    User user = transaction.run(Factory::createUser);
    Chat chat = transaction.run(() -> Factory.createPrivateChat(user));

    List<LocalDateTime> dates = new ArrayList<>();
    LocalDateTime start = LocalDateTime.now();
    for (int i = 0; i < 100; i++) {
      dates.add(start.minusDays(1));
    }

    ArrayList<LocalDateTime> shuffledDates = new ArrayList<>(dates);
    Collections.shuffle(shuffledDates);

    transaction.run(() ->
      shuffledDates.forEach(dateTime -> chat.sendMessage(user, "hello world", dateTime))
    );

    List<Message> messages = chat.findMessages().list();
    assertEquals(dates, messages.stream().map(Message::getSentAt).collect(Collectors.toList()));
  }


}
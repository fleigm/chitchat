package de.fleigm.chitchat;

import de.fleigm.chitchat.chat.GroupChat;
import de.fleigm.chitchat.chat.PrivateChat;
import de.fleigm.chitchat.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static de.fleigm.chitchat.Helper.faker;

public interface Factory {


  static User createUser() {
    User user = new User(UUID.randomUUID(), faker().name().username());
    user.persist();
    return user;
  }

  static List<User> createUsers(int amount) {
    return createMultiple(Factory::createUser, amount);
  }

  static PrivateChat createPrivateChat() {
    return createPrivateChat(createUser());
  }

  static PrivateChat createPrivateChat(User user) {
    User other = createUser();
    PrivateChat chat = faker().bool().bool() ? new PrivateChat(user, other) : new PrivateChat(other, user);
    chat.persist();
    return chat;
  }

  static List<PrivateChat> createPrivateChats(int amount) {
    return createPrivateChats(createUser(), amount);
  }

  static List<PrivateChat> createPrivateChats(User user, int amount) {
    return createMultiple(() -> createPrivateChat(user), amount);
  }

  static GroupChat createGroupChat() {
    return createGroupChat(createUser());
  }

  static GroupChat createGroupChat(User user) {
    int members = faker().number().numberBetween(0, 10);
    GroupChat chat = new GroupChat(user, faker().book().title());
    for (int i = 0; i < members; i++) {
      chat.add(createUser());
    }
    chat.persist();
    return chat;
  }

  static List<GroupChat> createGroupChats(int amount) {
    return createMultiple(() -> createGroupChat(createUser()), amount);
  }

  static List<GroupChat> createGroupChats(User user, int amount) {
    return createMultiple(() -> createGroupChat(user), amount);
  }

  private static <T> List<T> createMultiple(Supplier<T> supplier, int amount) {
    List<T> items = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      items.add(supplier.get());
    }
    return items;
  }
}

package de.fleigm.chitchat.chat;

import de.fleigm.chitchat.ApplicationException;
import de.fleigm.chitchat.EntityNotFoundException;
import de.fleigm.chitchat.users.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import lombok.Getter;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base entity class for {@link PrivateChat} und {@link GroupChat}
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "chat_type", discriminatorType = DiscriminatorType.STRING)
@Getter
public abstract class Chat extends PanacheEntityBase {

  @Id
  private UUID id = UUID.randomUUID();

  @OneToOne
  private Message lastMessage;

  public Optional<Message> getLastMessage() {
    return Optional.ofNullable(lastMessage);
  }

  /**
   * @see Chat#sendMessage(User, String, LocalDateTime)
   */
  public Message sendMessage(User user, String text) {
    return sendMessage(user, text, LocalDateTime.now());
  }

  /**
   * Create and persist a new {@link Message} if the user is allowed to
   * send a message to this chat.
   * If the user is not allowed a {@link ApplicationException} with the status code 403 will be thrown.
   *
   * @param user   who sends the message.
   * @param text   text
   * @param sentAt sent at
   * @return message
   */
  public Message sendMessage(User user, String text, LocalDateTime sentAt) {
    if (!canSendMessage(user)) {
      throw new ApplicationException("User is not allowed to send message to this chat", 403);
    }

    Message message = new Message(text, sentAt, user.getId(), this.id);
    this.lastMessage = message;
    message.persist();
    return message;
  }

  /**
   * @see Chat#canSendMessage(User)
   */
  public boolean canSendMessage(UUID id) {
    return canSendMessage(new User(id, ""));
  }

  /**
   * Check if a user can send a message to this chat.
   *
   * @param user user to check
   * @return true if user can send message, otherwise false
   */
  public abstract boolean canSendMessage(User user);

  /**
   * Find messages from this chat.
   *
   * @return query
   */
  public PanacheQuery<Message> findMessages() {
    return findMessages(id);
  }

  /**
   * Query for finding messages of a chat.
   *
   * @param chatId chat id
   * @return message query
   */
  public static PanacheQuery<Message> findMessages(UUID chatId) {
    return Message.find("chat", Sort.descending("sentAt"), chatId);
  }

  /**
   * Get a chat by its id or throw a {@link EntityNotFoundException} if the chat does not exists.
   *
   * @param id id
   * @return chat
   */
  public static Chat findByIdOrFail(Object id) {
    Chat chat = findById(id);

    if (chat == null) {
      throw EntityNotFoundException.of(id, Chat.class);
    }

    return chat;
  }

  /**
   * Query all chats of a given user.
   *
   * @param user user.
   * @return chats of given user.
   */
  public static List<Chat> findByUser(User user) {
    Stream<PrivateChat> privateChats = PrivateChat.findByParticipant(user.getId()).stream();
    Stream<GroupChat> groupChats = GroupChat.findByMember(user.getId()).stream();

    return Stream.concat(privateChats, groupChats)
        .sorted(Comparator.comparing(chat -> chat.getLastMessage().map(Message::getSentAt).orElse(LocalDateTime.MIN)))
        .collect(Collectors.toList());
  }
}

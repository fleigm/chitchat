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

  public Message sendMessage(User user, String text) {
    return sendMessage(user, text, LocalDateTime.now());
  }

  public Message sendMessage(User user, String text, LocalDateTime sentAt) {
    if (!canSendMessage(user)){
      throw new ApplicationException("User is not allowed to send message to this chat", 403);
    }

    Message message = new Message(text, sentAt, user, this);
    this.lastMessage = message;
    message.persist();
    return message;
  }

  public abstract boolean canSendMessage(User user);

  public PanacheQuery<Message> findMessages() {
    return findMessages(id);
  }

  public static PanacheQuery<Message> findMessages(UUID chatId) {
    return Message.find("chat.id", Sort.descending("sentAt"), chatId);
  }

  public static Chat findByIdOrFail(Object id) {
    Chat chat = findById(id);

    if (chat == null) {
      throw EntityNotFoundException.of(id, Chat.class);
    }

    return chat;
  }

  public static List<Chat> findByUser(User user) {
    Stream<PrivateChat> privateChats = PrivateChat.findByParticipant(user.getId()).stream();
    Stream<GroupChat> groupChats = GroupChat.findByMember(user.getId()).stream();

    return Stream.concat(privateChats, groupChats)
        .sorted(Comparator.comparing(chat -> chat.getLastMessage().map(Message::getSentAt).orElse(LocalDateTime.MIN)))
        .collect(Collectors.toList());
  }
}

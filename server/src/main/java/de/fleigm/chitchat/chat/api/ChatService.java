package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.Message;
import de.fleigm.chitchat.users.User;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.UUID;

/**
 * Only used for {@link de.fleigm.chitchat.chat.api.socket.ChatEndpoint} because
 * transactions are not allowed at web socket endpoints.
 */
@ApplicationScoped
public class ChatService {

  @Transactional
  public Message sendMessage(UUID senderId, UUID chatId, String text) {
    User sender = User.findByIdOrFail(senderId);
    Chat chat = Chat.findByIdOrFail(chatId);

    return chat.sendMessage(sender, text);
  }

  public Chat getChat(UUID id) {
    return Chat.findByIdOrFail(id);
  }
}

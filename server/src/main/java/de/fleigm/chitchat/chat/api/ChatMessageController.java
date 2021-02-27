package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.EntityNotFoundException;
import de.fleigm.chitchat.auth.AuthenticationService;
import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.Message;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("chats/{id}/messages")
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatMessageController {

  @Inject
  AuthenticationService auth;


  @POST
  @Transactional
  public Message sendMessage(@PathParam("id") UUID chatId, SendMessageRequest request) {
    Chat chat = Chat.findByIdOrFail(chatId);

    return chat.sendMessage(auth.currentUser(), request.getText());
  }

  @GET
  public List<Message> getMessages(@PathParam("id") UUID chatId) {
    Chat chat = Chat.findByIdOrFail(chatId);

    if (!chat.canSendMessage(auth.currentUser())) {
      throw new EntityNotFoundException(chatId, Chat.class);
    }

    return Message.find("chat", chat).list();
  }


}

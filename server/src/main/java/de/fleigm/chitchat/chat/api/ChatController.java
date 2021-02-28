package de.fleigm.chitchat.chat.api;


import de.fleigm.chitchat.auth.AuthenticationService;
import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.GroupChat;
import de.fleigm.chitchat.chat.PrivateChat;
import de.fleigm.chitchat.users.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("chats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ChatController {

  @Inject
  AuthenticationService auth;

  @GET
  public List<Chat> index() {
    User currentUser = auth.currentUser();

    return Chat.findByUser(currentUser);
  }

  @POST
  @Transactional
  public Chat create(CreateChatRequest request) {
    Chat chat;

    if (request instanceof CreateChatRequest.Private) {
      chat = createPrivateChat((CreateChatRequest.Private) request);
    } else {
      chat = createGroupChat((CreateChatRequest.Group) request);
    }

    chat.persist();

    return chat;
  }

  private Chat createPrivateChat(CreateChatRequest.Private request) {
    User user = User.findByIdOrFail(request.getUser());

    PrivateChat existingChat = PrivateChat.findByParticipants(user.getId(), auth.currentUser().getId());

    if (existingChat != null) {
      return existingChat;
    }

    return new PrivateChat(auth.currentUser(), user);
  }

  private Chat createGroupChat(CreateChatRequest.Group request) {
    GroupChat chat = new GroupChat(auth.currentUser(), request.getName());

    List<User> users = User.find("id in ?1", request.getMembers()).list();
    users.forEach(chat::add);

    return chat;
  }
}

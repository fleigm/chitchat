package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.EntityNotFoundException;
import de.fleigm.chitchat.auth.AuthenticationService;
import de.fleigm.chitchat.chat.GroupChat;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("chats/{id}")
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupChatController {

  @Inject
  AuthenticationService auth;

  @Path("join")
  @POST
  @Transactional
  public GroupChat join(@PathParam("id") UUID id) {
    GroupChat chat = GroupChat.findById(id);

    if (chat == null) {
      throw EntityNotFoundException.of(id, GroupChat.class);
    }

    chat.add(auth.currentUser());

    return chat;
  }

  @Path("leave")
  @POST
  @Transactional
  public void leave(@PathParam("id") UUID id) {
    GroupChat chat = GroupChat.findById(id);

    if (chat == null) {
      throw EntityNotFoundException.of(id, GroupChat.class);
    }

    chat.remove(auth.currentUser());
  }

}

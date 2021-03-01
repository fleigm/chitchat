package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.EntityNotFoundException;
import de.fleigm.chitchat.auth.AuthenticationService;
import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.Message;
import de.fleigm.chitchat.chat.api.socket.ChatMessage;
import de.fleigm.chitchat.http.pagination.Page;
import de.fleigm.chitchat.http.pagination.Pagination;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
  public Page<ChatMessage> getMessages(@PathParam("id") UUID chatId,
                                   @BeanParam Pagination pagination,
                                   @Context UriInfo uriInfo) {

    Chat chat = Chat.findByIdOrFail(chatId);

    if (!chat.canSendMessage(auth.currentUser())) {
      throw new EntityNotFoundException(chatId, Chat.class);
    }

    // TODO same format for rest and socket endpoint!!!
    PanacheQuery<Message> query = chat.findMessages();

    List<ChatMessage> entries = query.stream().map(message -> new ChatMessage(
        message.getSender().getId(),
        message.getChat().getId(),
        message.getText(),
        message.getSentAt()
    )).collect(Collectors.toList());

    return Page.<ChatMessage>builder()
        .entries(entries)
        .currentPage(pagination.getPage())
        .pageSize(pagination.getPageSize())
        .total(query.count())
        .uri(uriInfo.getAbsolutePath())
        .build();

    //return Page.create(chat.findMessages(), pagination, uriInfo);
  }


}

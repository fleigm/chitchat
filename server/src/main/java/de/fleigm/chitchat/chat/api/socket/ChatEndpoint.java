package de.fleigm.chitchat.chat.api.socket;

import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.Message;
import de.fleigm.chitchat.chat.api.ChatService;
import de.fleigm.chitchat.chat.socket.SocketAuthentication;
import de.fleigm.chitchat.chat.socket.Token;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(
    value = "/chat/{token}",
    encoders = MessageEncoder.class,
    decoders = MessageDecoder.class)
public class ChatEndpoint {
  private static final Logger LOGGER = Logger.getLogger(ChatEndpoint.class);

  Map<String, Session> sessions = new ConcurrentHashMap<>();

  @Inject
  SocketAuthentication socketAuthentication;

  @Inject
  ChatService chatService;

  @OnOpen
  public void onOpen(Session session, @PathParam("token") String authToken) throws IOException {
    Token token = Token.fromString(authToken);

    if (!socketAuthentication.hasToken(token)) {
      session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "unauthorized"));
    }

    UUID userId = socketAuthentication.useTokenAndGetUser(token);
    session.getUserProperties().put("user", userId);
    sessions.put(session.getId(), session);
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session.getId());
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    LOGGER.error("failed", throwable);
    sessions.remove(session.getId());
  }

  @OnMessage
  public void onMessage(Session session, ChatMessage message) {
    Message msg = chatService.sendMessage(getUserId(session), message.getChat(), message.getText());

    message.setSender(msg.getSender().getId());
    message.setSentAt(msg.getSentAt());

    broadcast(message);
  }

  private UUID getUserId(Session session) {
    return (UUID) session.getUserProperties().get("user");
  }

  private void broadcast(ChatMessage message) {
    Chat chat = chatService.getChat(message.getChat());

    sessions.values()
        .stream()
        .filter(session -> chat.canSendMessage(getUserId(session)))
        .peek(session -> LOGGER.info("send to {}" + getUserId(session)))
        .forEach(session -> session.getAsyncRemote().sendObject(message, result ->  {
          if (result.getException() != null) {
            LOGGER.error("Could not send message", result.getException());
          }
        }));
  }

}

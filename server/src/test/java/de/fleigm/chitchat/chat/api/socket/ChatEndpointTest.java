package de.fleigm.chitchat.chat.api.socket;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Helper;
import de.fleigm.chitchat.MockAuthorizationServer;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.chat.Message;
import de.fleigm.chitchat.chat.PrivateChat;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MockAuthorizationServer.class)
class ChatEndpointTest {

  @TestHTTPResource("/chat")
  URI uri;

  @Inject
  Transaction transaction;

  @Test
  public void can_send_and_receive_message() throws Exception {
    User user = Helper.createUser("john");
    PrivateChat chat = transaction.run(() -> Factory.createPrivateChat(user));

    ChatClient chatClient = new ChatClient().forUser(user).start(uri);

    chatClient.sendMessage(chat.getId(), "hello world");

    Message message = chatClient.messages.poll(10, TimeUnit.SECONDS);
    assertEquals(user.getId(), message.getSender());
    assertEquals("hello world", message.getText());
    assertEquals(chat.getId(), message.getChat());

    List<Message> messages = chat.findMessages().list();
    assertEquals(1, messages.size());
  }

  @Test
  void only_get_messages_from_own_chat() throws InterruptedException {
    User john = Helper.createUser("john");
    User alice = Helper.createUser("alice");
    PrivateChat chat = transaction.run(() -> Factory.createPrivateChat(alice));

    ChatClient johnClient = new ChatClient().forUser(john).start(uri);
    ChatClient aliceClient = new ChatClient().forUser(alice).start(uri);

    aliceClient.sendMessage(chat.getId(), "hello");
    assertNotNull(aliceClient.messages.poll(10, TimeUnit.SECONDS));
    assertNull(johnClient.messages.poll(500, TimeUnit.MILLISECONDS));
  }


  @ClientEndpoint(decoders = MessageDecoder.class, encoders = MessageEncoder.class)
  public static class ChatClient {
    public User user;
    public Session session;
    public LinkedBlockingDeque<Message> messages = new LinkedBlockingDeque<>();

    public ChatClient forUser(User user) {
      this.user = user;
      return this;
    }

    public ChatClient start(URI endpoint) {
      try {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(
            this,
            UriBuilder.fromUri(endpoint).path(getAuthenticationToken(user)).build());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      return this;
    }

    @OnMessage
    void message(WebSocketMessage webSocketMessage) {
      messages.add(((BroadcastMessagePayload) webSocketMessage.getPayload()).getMessage());
    }

    public ChatClient sendMessage(UUID chatId, String message) {
      session.getAsyncRemote().sendObject(new WebSocketMessage(new SendMessagePayload(chatId, message)));
      return this;
    }

    private String getAuthenticationToken(User user) {
      return given()
          .contentType(ContentType.JSON)
          .auth().oauth2(Helper.generateJWT(user))
          .when()
          .post("sockets")
          .then()
          .statusCode(200)
          .extract()
          .path("id");
    }
  }

}
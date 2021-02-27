package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Helper;
import de.fleigm.chitchat.MockAuthorizationServer;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.Message;
import de.fleigm.chitchat.chat.PrivateChat;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MockAuthorizationServer.class)
class ChatMessageControllerTest {

  @Inject
  Transaction transaction;

  @Test
  void send_private_message() {
    User user = transaction.run(Factory::createUser);
    PrivateChat chat = transaction.run(() -> Factory.createPrivateChat(user));

    Message message = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(user))
        .body(new SendMessageRequest("hello world"))
        .when()
        .post("chats/" + chat.getId() + "/messages")
        .then()
        .statusCode(200)
        .extract()
        .as(Message.class);

    assertEquals("hello world", message.getText());
    assertEquals(user, message.getSender());
    assertNotNull(message.getSentAt());

    assertTrue(Chat.<Chat>findById(chat.getId()).getLastMessage().isPresent());
  }

}
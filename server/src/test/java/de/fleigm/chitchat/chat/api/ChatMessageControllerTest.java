package de.fleigm.chitchat.chat.api;

import com.github.javafaker.Faker;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
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

  @Test
  void get_messages_newest_to_oldest() {
    Faker faker = Helper.faker();

    User user = transaction.run(Factory::createUser);
    Chat chat = transaction.run(() -> Factory.createPrivateChat(user));

    transaction.run(() -> {
      for (int i = 0; i < 100; i++) {
        chat.sendMessage(user, UUID.randomUUID().toString(), createFakeLocalDateTime(faker));
      }
    });

    List<Message> messages = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(user))
        .queryParam("page", 1)
        .queryParam("page-size", 20)
        .when()
        .get("chats/" + chat.getId() + "/messages")
        .then()
        .statusCode(200)
        .body("total", is(100))
        .extract()
        .body()
        .jsonPath()
        .getList("entries", Message.class);

    assertEquals(20, messages.size());
  }

  private LocalDateTime createFakeLocalDateTime(Faker faker) {
    return faker.date().past(100, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

}
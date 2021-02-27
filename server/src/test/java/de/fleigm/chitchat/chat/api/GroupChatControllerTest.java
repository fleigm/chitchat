package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Helper;
import de.fleigm.chitchat.MockAuthorizationServer;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.chat.GroupChat;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(MockAuthorizationServer.class)
class GroupChatControllerTest {

  @Inject
  Transaction transaction;

  @Test
  void user_can_join_group() {
    User user = transaction.run(Factory::createUser);
    GroupChat groupChat = transaction.run((Supplier<GroupChat>) Factory::createGroupChat);

    GroupChat group = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(user))
        .when()
        .post("chats/" + groupChat.getId() + "/join")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupChat.class);

    assertTrue(group.isMember(user));
  }

  @Test
  void user_can_leave_group() {
    User user = transaction.run(Factory::createUser);
    GroupChat groupChat = transaction.run(() -> Factory.createGroupChat(user));

    given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(user))
        .when()
        .post("chats/" + groupChat.getId() + "/leave")
        .then()
        .statusCode(204);

    GroupChat chat = GroupChat.findById(groupChat.getId());
    assertFalse(chat.isMember(user));
  }

}
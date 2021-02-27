package de.fleigm.chitchat.chat.api;

import de.fleigm.chitchat.Factory;
import de.fleigm.chitchat.Helper;
import de.fleigm.chitchat.MockAuthorizationServer;
import de.fleigm.chitchat.Transaction;
import de.fleigm.chitchat.chat.Chat;
import de.fleigm.chitchat.chat.GroupChat;
import de.fleigm.chitchat.chat.PrivateChat;
import de.fleigm.chitchat.users.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(MockAuthorizationServer.class)
class ChatControllerTest {

  @Inject
  Transaction transaction;

  @Test
  void create_private_chat() {
    User john = Helper.createUser("john");
    User alice = Helper.createUser("alice");

    Response response = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(john))
        .body(new CreateChatRequest.Private(alice.getId()))
        .when()
        .post("chats");

    response.then().statusCode(200);

    PrivateChat chat = response.as(PrivateChat.class);

    assertNotNull(chat);
    assertEquals(john.getId(), chat.getParticipantA().getId());
    assertEquals(alice.getId(), chat.getParticipantB().getId());
    assertNotNull(PrivateChat.findByParticipants(john.getId(), alice.getId()));
  }

  @Test
  void create_private_chat_fails_if_other_user_does_not_exists() {
    User john = Helper.createUser("john");
    UUID unknownUserId = UUID.randomUUID();

    given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(john))
        .body(new CreateChatRequest.Private(unknownUserId))
        .when()
        .post("chats")
        .then()
        .statusCode(404);

    assertNull(PrivateChat.findByParticipants(john.getId(), unknownUserId));
  }

  @Test
  void create_group_chat() {
    User john = Helper.createUser("john");
    GroupChat chat = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(john))
        .body(new CreateChatRequest.Group("new group", Collections.emptyList()))
        .when()
        .post("chats")
        .then()
        .statusCode(200)
        .extract()
        .as(GroupChat.class);

    assertEquals("new group", chat.getName());
    assertTrue(chat.isMember(john));
    assertEquals(1, chat.getMembers().size());
    assertNotNull(Chat.findById(chat.getId()));
  }

  @Test
  void create_group_with_members() {
    User john = Helper.createUser("john");

    List<User> users = transaction.run(() -> Factory.createUsers(10));

    GroupChat chat = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(john))
        .body(new CreateChatRequest.Group("new group", users.stream().map(User::getId).collect(Collectors.toList())))
        .when()
        .post("chats")
        .then()
        .statusCode(200)
        .extract()
        .as(GroupChat.class);

    assertEquals(11, chat.getMembers().size());
    assertNotNull(GroupChat.findById(chat.getId()));
  }

  @Test
  void create_group_with_members_ignore_unknown_users() {
    User john = Helper.createUser("john");

    List<UUID> userIds = transaction
        .run(() -> Factory.createUsers(10))
        .stream()
        .map(User::getId)
        .collect(Collectors.toList());

    userIds.add(UUID.randomUUID());
    userIds.add(UUID.randomUUID());

    GroupChat chat = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(john))
        .body(new CreateChatRequest.Group("new group", userIds))
        .when()
        .post("chats")
        .then()
        .statusCode(200)
        .extract()
        .as(GroupChat.class);

    assertEquals(11, chat.getMembers().size());
    assertNotNull(GroupChat.findById(chat.getId()));
  }

  @Test
  void get_all_my_chats() {
    User user = transaction.run(Factory::createUser);
    transaction.run(() -> Factory.createPrivateChats(user, 5));
    transaction.run(() -> Factory.createGroupChats(user, 5));

    transaction.run(() -> Factory.createPrivateChats(2));
    transaction.run(() -> Factory.createGroupChats(2));

    List<?> chats = given()
        .contentType(ContentType.JSON)
        .auth().oauth2(Helper.generateJWT(user))
        .when()
        .get("chats")
        .then()
        .statusCode(200)
        .extract()
        .as(List.class);

    assertEquals(10, chats.size());
  }

  @Test
  void fail_if_not_authenticated() {
    given().when().get("chats").then().statusCode(401);

    given()
        .auth().oauth2("invalid_token")
        .when().get("chats")
        .then().statusCode(401);

    given()
        .contentType(ContentType.JSON)
        .when().post("chats").then().statusCode(401);

    given()
        .contentType(ContentType.JSON)
        .auth().oauth2("invalid_token")
        .when().post("chats")
        .then().statusCode(401);
  }


}
package de.fleigm.realtalk.users;

import de.fleigm.realtalk.Helper;
import de.fleigm.realtalk.MockAuthorizationServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestHTTPEndpoint(UserController.class)
@QuarkusTestResource(MockAuthorizationServer.class)
public class UserControllerTest {

  @Test
  void automatically_create_user_if_it_not_exists() {
    UUID id = UUID.randomUUID();
    String newUserToken = Helper.generateJWT(id, "new user", "user");

    Response response = given().auth().oauth2(newUserToken)
        .when()
        .get("me");

    response.then().statusCode(200);
    User user = response.as(User.class);

    assertEquals(id, user.id);
    assertEquals("new user", user.username);
    assertNotNull(User.findById(user.id));
  }
}

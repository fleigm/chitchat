package de.fleigm.chitchat;

import com.github.javafaker.Faker;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.fleigm.chitchat.users.User;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public interface Helper {

  static Faker faker() {
    return new Faker();
  }

  static User createUser(String username) {
    Response response = given()
        .auth().oauth2(generateJWT(username, "user"))
        .when()
        .get("/users/me");

    assertEquals(200, response.statusCode());

    return response.as(User.class);
  }

  static String generateJWT(User user) {
    return generateJWT(user.getId(), user.getUsername(), "user");
  }

  static String generateJWT(String username, String... roles) {
    return generateJWT(UUID.randomUUID(), username, roles);
  }

  static String generateJWT(UUID id, String username, String... roles) {
    // Prepare JWT with claims set
    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(MockAuthorizationServer.keyPair.getKeyID())
            .type(JOSEObjectType.JWT)
            .build(),
        new JWTClaimsSet.Builder()
            .subject(id.toString())
            .issuer("https://wiremock")
            .claim(
                "realm_access",
                new JWTClaimsSet.Builder()
                    .claim("roles", Arrays.asList(roles))
                    .build()
                    .toJSONObject()
            )
            .claim("preferred_username", username)
            .claim("scope", "openid email profile")
            .expirationTime(new Date(new Date().getTime() + 60 * 1000))
            .build()
    );
    // Compute the RSA signature
    try {
      signedJWT.sign(new RSASSASigner(MockAuthorizationServer.keyPair.toRSAKey()));
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
    return signedJWT.serialize();
  }
}

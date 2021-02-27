package de.fleigm.chitchat;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class Helper {

  public static String generateJWT(String username, String... roles) {
    return generateJWT(UUID.randomUUID(), username, roles);
  }

  public static String generateJWT(UUID id, String username, String... roles) {
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

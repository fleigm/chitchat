package de.fleigm.chitchat.auth;

import de.fleigm.chitchat.users.User;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.UUID;

@RequestScoped
public class AuthenticationService {

  @Inject
  SecurityIdentity identity;

  @Inject
  JsonWebToken token;


  @Produces
  @RequestScoped
  @CurrentUser
  public User currentUser() {
    return User.findById(UUID.fromString(token.getSubject()));
  }

}

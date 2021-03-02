package de.fleigm.chitchat.users;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

/**
 * On each authenticated request chek if the user already exits in the database and create it if not.
 * This is needed because user register and authenticate via keycloak
 * but we still need a user representation in our system.
 */
@Provider
public class UserCreationFilter implements ContainerRequestFilter {
  private static final Logger LOGGER = Logger.getLogger(UserCreationFilter.class);

  @Inject
  SecurityIdentity identity;

  @Inject
  JsonWebToken token;

  @Inject
  EnsureUserExists ensureUserExists;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (identity.isAnonymous()) {
      return;
    }

    UUID id = UUID.fromString(token.getSubject());
    String username = identity.getPrincipal().getName();

    // this is to handle the race condition
    // if a new user sends multiple requests at once we might
    // try to insert the user multiple times resulting in a duplicate primary key exception.
    try {
      ensureUserExists.run(id, username);
    } catch (Exception e) {
      LOGGER.warn("race condition");
    }
  }

  /**
   * Do not inline!!
   * otherwise we cannot catch the duplicate primary key exception
   */
  @ApplicationScoped
  protected static class EnsureUserExists {

    @Transactional
    public void run(UUID id, String username) {
      if (User.findByIdOptional(id).isEmpty()) {
        User user = new User(id, username);
        user.persist();
      }
    }
  }
}

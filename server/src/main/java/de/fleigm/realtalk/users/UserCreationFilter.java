package de.fleigm.realtalk.users;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.UUID;

@Provider
public class UserCreationFilter implements ContainerRequestFilter {

  @Inject
  SecurityIdentity identity;

  @Inject
  JsonWebToken token;

  @Transactional
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (identity.isAnonymous()) {
      return;
    }

    UUID id = UUID.fromString(token.getSubject());
    String username = identity.getPrincipal().getName();

    if (User.findByIdOptional(id).isEmpty()) {
      User user = new User(id, username);
      user.persist();
    }
  }
}

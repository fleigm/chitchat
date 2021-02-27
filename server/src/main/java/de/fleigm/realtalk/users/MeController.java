package de.fleigm.realtalk.users;

import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("users/me")
@Produces(MediaType.APPLICATION_JSON)
public class MeController {

  @Inject
  SecurityIdentity identity;

  @Inject
  JsonWebToken token;


  @GET
  @RolesAllowed("user")
  public Response get() {
    User user = User.findById(UUID.fromString(token.getSubject()));

    return Response.ok(user).build();
  }


}

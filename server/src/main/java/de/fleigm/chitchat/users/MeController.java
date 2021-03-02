package de.fleigm.chitchat.users;

import de.fleigm.chitchat.auth.AuthenticationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users/me")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
public class MeController {

  @Inject
  AuthenticationService auth;

  /**
   * @return authenticated user.
   */
  @GET
  public Response get() {
    return Response.ok(auth.currentUser()).build();
  }


}

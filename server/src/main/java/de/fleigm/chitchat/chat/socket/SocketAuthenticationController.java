package de.fleigm.chitchat.chat.socket;

import de.fleigm.chitchat.auth.AuthenticationService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Allows authenticated users to request a token that is used to establish a web socket connection.
 */
@Path("sockets")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SocketAuthenticationController {

  @Inject
  SocketAuthentication socketAuthentication;

  @Inject
  AuthenticationService auth;

  @POST
  public Token generateSocketAuthenticationToken() {
    Token token = socketAuthentication.generateTokenForUser(auth.currentUser());

    return token;
  }
}

package de.fleigm.chitchat.chat.socket;

import de.fleigm.chitchat.users.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The browsers do not support adding headers to a web socket handshake request so we cannot use our
 * normal authentication flow for web sockets.
 * <p>
 * To connect to a web socket a user has to request a {@link Token} at the {@link SocketAuthenticationController}
 * that can be used exactly one time.
 * If a token is generated we store it along with the user who requested it.
 * On connecting to the web socket we use this token to link the connection to a user.
 * The token will be deleted once the connection is established.
 */
@ApplicationScoped
public class SocketAuthentication {
  private final Map<Token, UUID> userTokens = new ConcurrentHashMap<>();

  /**
   * Generate a one time {@link Token} for a given user.
   *
   * @param user requesting user
   * @return token
   */
  public Token generateTokenForUser(User user) {
    Token token = Token.generate();
    userTokens.put(token, user.getId());
    return token;
  }

  /**
   * Remove token and return the user it belongs to.
   *
   * @param token token
   * @return user
   */
  public UUID useTokenAndGetUser(Token token) {
    return userTokens.remove(token);
  }

  public void removeToken(Token token) {
    userTokens.remove(token);
  }

  public boolean hasToken(Token token) {
    return userTokens.containsKey(token);
  }

  public UUID getUserFromToken(Token token) {
    return userTokens.get(token);
  }
}

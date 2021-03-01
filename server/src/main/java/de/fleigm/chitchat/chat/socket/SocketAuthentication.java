package de.fleigm.chitchat.chat.socket;

import de.fleigm.chitchat.users.User;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SocketAuthentication {
  private final Map<Token, UUID> userTokens = new ConcurrentHashMap<>();

  public Token generateTokenForUser(User user) {
    Token token = Token.generate();
    userTokens.put(token, user.getId());
    return token;
  }

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

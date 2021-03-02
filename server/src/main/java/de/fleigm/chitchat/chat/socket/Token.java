package de.fleigm.chitchat.chat.socket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * Authentication token to establish a web socket connection.
 */
public class Token {
  public final UUID id;

  @JsonCreator
  public Token(@JsonProperty("id") String id) {
    this(UUID.fromString(id));
  }

  private Token(UUID id) {
    this.id = id;
  }

  public static Token generate() {
    return new Token(UUID.randomUUID());
  }

  public static Token fromString(String token) {
    return new Token(UUID.fromString(token));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Token)) return false;
    Token token = (Token) o;
    return Objects.equals(id, token.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return id.toString();
  }
}

package de.fleigm.chitchat.http.search;

public enum Operation {
  EQUALITY,
  GREATER_THAN,
  LESS_THAN,
  LIKE,
  NONE;

  public static Operation get(char value) {
    switch (value) {
      case ':':
        return EQUALITY;
      case '<':
        return LESS_THAN;
      case '>':
        return GREATER_THAN;
      case '~':
        return LIKE;
      default:
        return NONE;
    }
  }
}

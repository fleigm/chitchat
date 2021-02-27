package de.fleigm.chitchat;

public class ApplicationException extends RuntimeException {
  private int code;

  public ApplicationException(String message, int code) {
    super(message);
    this.code = code;
  }
}

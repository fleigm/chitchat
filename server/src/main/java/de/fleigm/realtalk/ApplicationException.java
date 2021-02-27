package de.fleigm.realtalk;

public class ApplicationException extends RuntimeException {
  private int code;

  public ApplicationException(String message, int code) {
    super(message);
    this.code = code;
  }
}

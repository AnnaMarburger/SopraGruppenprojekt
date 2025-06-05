package exceptions;

/**
 * This exception is used when there is a problem concerning the server itself.
 */
public class ServerErrorException extends RuntimeException {

  public ServerErrorException(String message) {
    super(message);
  }
}

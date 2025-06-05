package exceptions;

/**
 * This exception is used when there is a problem concerning the player numbers.
 */
public class InvalidPlayerNumberException extends Exception {

  public InvalidPlayerNumberException() {
    super("Invalid player number!");
  }
}

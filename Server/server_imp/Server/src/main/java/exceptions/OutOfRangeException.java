package exceptions;

/**
 * This exception is used when there is a problem concerning the range between to positions.
 */
public class OutOfRangeException extends Exception {

  public OutOfRangeException() {
    super("Out of range!");
  }

  public OutOfRangeException(String message) {
    super(message);
  }
}

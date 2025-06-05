package exceptions;

/**
 * This Exception is used for inconsistencies in the game data.
 */
public class InconsistentDataException extends Exception {

  public InconsistentDataException() {
    super("Inconsistent data!");
  }

  public InconsistentDataException(String message) {
    super(message);
  }
}

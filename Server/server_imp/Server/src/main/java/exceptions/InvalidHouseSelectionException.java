package exceptions;

/**
 * This exception is used when a client selects an invalid house is the house selection phase.
 */
public class InvalidHouseSelectionException extends Exception {

  public InvalidHouseSelectionException() {
    super("Invalid house selection!");
  }
}

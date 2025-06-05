package exceptions;

/**
 * This exception is used for cases where the client is not allowed to execute an action.
 */
public class UnableToExecuteActionException extends Exception {

  public UnableToExecuteActionException(String message) {
    super(message);
  }
}

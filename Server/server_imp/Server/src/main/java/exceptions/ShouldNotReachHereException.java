package exceptions;

/**
 * This exception is used for cases the server shouldn't reach.
 */
public class ShouldNotReachHereException extends Exception {

  public ShouldNotReachHereException() {
    super("Should not reach here!");
  }
}

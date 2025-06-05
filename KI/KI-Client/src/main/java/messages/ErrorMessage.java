package messages;

import messages.data.MessageType;

/**
 * This class represents an error message.
 */
public class ErrorMessage extends Message {

  public final int errorCode; // identification code
  // 001 - bad format
  // 002 - isActive and isCpu not match
  // 003 - already two players registered
  // 004 - unknown client secret
  // 005 - an unknown error occurred
  public final String errorDescription; // description of error

  public ErrorMessage(final int errorCode, final String errorDescription) {
    super(MessageType.ERROR);
    this.errorCode = errorCode;
    this.errorDescription = errorDescription;
  }
}

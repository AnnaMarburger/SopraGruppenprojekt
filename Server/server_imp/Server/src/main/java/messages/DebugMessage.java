package messages;

import messages.data.MessageType;

/**
 * This class represents a debug message.
 */
public class DebugMessage extends Message {

  public final int code; // identification number
  public final String explanation; // explanation of event

  public DebugMessage(final int code, final String explanation) {
    super(MessageType.DEBUG);
    this.code = code;
    this.explanation = explanation;
  }
}

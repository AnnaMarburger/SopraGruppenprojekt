package messages;

import messages.data.MessageType;

/**
 * This class represents a pause request message.
 */
public class PauseRequestMessage extends Message {

  public final boolean pause; // pause

  public PauseRequestMessage(final boolean pause) {
    super(MessageType.PAUSE_REQUEST);
    this.pause = pause;
  }
}

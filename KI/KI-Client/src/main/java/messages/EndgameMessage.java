package messages;

import messages.data.MessageType;

/**
 * This class represents an endgame message.
 * Activated over-length mechanism.
 */
public class EndgameMessage extends Message {

  public EndgameMessage() {
    super(MessageType.ENDGAME);
  }
}

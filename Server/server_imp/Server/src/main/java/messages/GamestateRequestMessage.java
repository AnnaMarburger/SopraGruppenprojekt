package messages;

import messages.data.MessageType;

/**
 * This class represents a game state request message.
 */
public class GamestateRequestMessage extends Message {

  public final int clientID; // client id

  public GamestateRequestMessage(final int clientID) {
    super(MessageType.GAMESTATE_REQUEST);
    this.clientID = clientID;
  }
}

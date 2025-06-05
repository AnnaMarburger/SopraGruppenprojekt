package messages;

import messages.data.MessageType;

/**
 * This class represents a turn demand message.
 */
public class TurnDemandMessage extends Message {

  public final int clientID; // next client id
  public final int characterID; // next character id

  public TurnDemandMessage(final int clientID, final int characterID) {
    super(MessageType.TURN_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
  }
}

package messages;

import messages.data.MessageType;

/**
 * This class represents an end turn request message
 */
public class EndTurnRequestMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id

  public EndTurnRequestMessage(final int clientID, final int CharacterID) {
    super(MessageType.END_TURN_REQUEST);
    this.clientID = clientID;
    this.characterID = CharacterID;
  }
}

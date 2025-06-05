package messages;

import messages.data.MessageType;
import messages.data.dictionary.MovementSpecs;

/**
 * This class represents a movement request message.
 */
public class MovementRequestMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final MovementSpecs specs; // specifications of the requested movement

  public MovementRequestMessage(final int clientID, final int characterID,
      final MovementSpecs specs) {
    super(MessageType.MOVEMENT_REQUEST);
    this.clientID = clientID;
    this.characterID = characterID;
    this.specs = specs;
  }
}
package messages;

import messages.data.MessageType;
import messages.data.dictionary.MovementSpecs;

/**
 * This class represents a movement demand message.
 */
public class MovementDemandMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final MovementSpecs specs; // specifications of the demanded movement

  public MovementDemandMessage(final int clientID, final int characterID,
      final MovementSpecs specs) {
    super(MessageType.MOVEMENT_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.specs = specs;
  }
}

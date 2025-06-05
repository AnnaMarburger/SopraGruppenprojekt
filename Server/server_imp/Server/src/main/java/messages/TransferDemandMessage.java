package messages;

import messages.data.MessageType;

/**
 * This class represents a transfer demand message.
 */
public class TransferDemandMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final int targetID; // target id

  public TransferDemandMessage(final int clientID, final int characterID, final int targetID) {
    super(MessageType.TRANSFER_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.targetID = targetID;
  }
}

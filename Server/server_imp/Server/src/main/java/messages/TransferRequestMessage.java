package messages;

import messages.data.MessageType;

/**
 * This class represents a transfer request message.
 */
public class TransferRequestMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final int targetID; // target id
  public final int amount; // amount of spice to transfer

  public TransferRequestMessage(final int clientID, final int characterID, final int targetID,
      final int amount) {
    super(MessageType.TRANSFER_REQUEST);
    this.clientID = clientID;
    this.characterID = characterID;
    this.targetID = targetID;
    this.amount = amount;
  }
}

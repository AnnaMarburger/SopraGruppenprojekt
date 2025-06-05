package messages;

import messages.data.MessageType;

/**
 * This class represents a strike message.
 */
public class StrikeMessage extends Message {

  public final int clientID; // struck client id
  public final String wrongMessage; // reason why the client got a strike
  public final int count; // amount af strikes

  public StrikeMessage(final int clientID, final String wrongMessage, final int count) {
    super(MessageType.STRIKE);
    this.clientID = clientID;
    this.wrongMessage = wrongMessage;
    this.count = count;
  }
}

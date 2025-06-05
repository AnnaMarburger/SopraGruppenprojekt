package messages;

import messages.data.MessageType;

/**
 * This class represents an un-pause game offer message.
 */
public class UnpauseGameOfferMessage extends Message {

  public final int requestedByClientID; // client id who requested the un-pause offer

  public UnpauseGameOfferMessage(final int requestedByClientID) {
    super(MessageType.UNPAUSE_GAME_OFFER);
    this.requestedByClientID = requestedByClientID;
  }
}

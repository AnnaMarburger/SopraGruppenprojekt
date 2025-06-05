package messages;

import messages.data.MessageType;

/**
 * This class represents a re-join message.
 */
public class ReJoinMessage extends Message {

  public final String clientSecret; // client secret

  protected ReJoinMessage(final String clientSecret) {
    super(MessageType.REJOIN);
    this.clientSecret = clientSecret;
  }
}

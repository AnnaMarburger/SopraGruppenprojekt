package messages;

import messages.data.MessageType;

/**
 * This class represents a join accepted message.
 */
public class JoinAcceptedMessage extends Message {

  public final String clientSecret; // client secret
  public final int clientID; // client id

  public JoinAcceptedMessage(final String clientSecret, final int clientID) {
    super(MessageType.JOINACCEPTED);
    this.clientSecret = clientSecret;
    this.clientID = clientID;
  }
}

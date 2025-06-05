package messages;

import messages.data.MessageType;

/**
 * This class represents a join message.
 */
public class JoinMessage extends Message {

  public final String clientName; // name of client
  public final boolean isActive; // specification if active player of spectator
  public final boolean isCpu; // specification if human player or ki player

  public JoinMessage(final String clientName, final boolean isActive, final boolean isCpu) {
    super(MessageType.JOIN);
    this.clientName = clientName;
    this.isActive = isActive;
    this.isCpu = isCpu;
  }
}

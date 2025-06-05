package messages;

import messages.data.MessageType;

/**
 * This class represents a change player spice demand message.
 */
public class ChangePlayerSpiceDemandMessage extends Message {

  public final int clientID; // client id
  public final int newSpiceValue; // new spice value of player

  public ChangePlayerSpiceDemandMessage(final int clientID, final int newSpiceValue) {
    super(MessageType.CHANGE_PLAYER_SPICE_DEMAND);
    this.clientID = clientID;
    this.newSpiceValue = newSpiceValue;
  }
}

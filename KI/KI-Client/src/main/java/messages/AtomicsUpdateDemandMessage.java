package messages;

import messages.data.MessageType;

/**
 * This class represents an atomics update demand message.
 */
public class AtomicsUpdateDemandMessage extends Message {

  public final int clientID; // client id
  public final boolean shunned; // boolean if the player with the client id is shunned
  public final int atomicsLeft; // how many atomics left

  public AtomicsUpdateDemandMessage(final int clientID, final boolean shunned,
      final int atomicsLeft) {
    super(MessageType.ATOMICS_UPDATE_DEMAND);
    this.clientID = clientID;
    this.shunned = shunned;
    this.atomicsLeft = atomicsLeft;
  }
}

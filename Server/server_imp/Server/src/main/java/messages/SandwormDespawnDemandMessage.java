package messages;

import messages.data.MessageType;

/**
 * This class represents a sand worm de-spawn demand message.
 */
public class SandwormDespawnDemandMessage extends Message {

  public SandwormDespawnDemandMessage() {
    super(MessageType.SANDWORM_DESPAWN_DEMAND);
  }
}

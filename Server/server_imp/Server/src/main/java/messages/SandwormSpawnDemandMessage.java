package messages;

import messages.data.MessageType;
import shared_data.Position;

/**
 * This class represents a sand worm spawn demand message.
 */
public class SandwormSpawnDemandMessage extends Message {

  public final int clientID; // targeted client id
  public final int characterID; // targeted character id
  public final Position position; // position of sand worm spawn

  public SandwormSpawnDemandMessage(final int clientID, final int characterID,
      final Position position) {
    super(MessageType.SANDWORM_SPAWN_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.position = position;
  }
}

package messages;

import messages.data.MessageType;
import messages.data.dictionary.Attributes;
import shared_data.Position;

/**
 * This class represents a spawn character demand message.
 */
public class SpawnCharacterDemandMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final String characterName;
  public final Position position; // position of spawned character
  public final Attributes attributes;

  public SpawnCharacterDemandMessage(final int clientID, final int characterID, final String characterName,
      final Position position, final Attributes attributes) {
    super(MessageType.SPAWN_CHARACTER_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.characterName = characterName;
    this.position = position;
    this.attributes = attributes;
  }
}

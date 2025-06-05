package messages;

import messages.data.MessageType;
import messages.data.dictionary.Stats;

/**
 * This class represents a character stat change demand message.
 */
public class CharacterStatChangeDemandMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final Stats stats; // stats of character

  public CharacterStatChangeDemandMessage(final int clientID, final int characterID,
      final Stats stats) {
    super(MessageType.CHARACTER_STAT_CHANGE_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.stats = stats;
  }
}

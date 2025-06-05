package messages.data.character;

import shared_data.character.CharacterType;

/**
 * This class represents a character is the message format.
 */
public class MessageCharacter {

  public final String characterName;
  public final CharacterType characterClass;

  public MessageCharacter(final String characterName, final CharacterType characterClass) {
    this.characterName = characterName;
    this.characterClass = characterClass;
  }

  @Override
  public String toString() {
    return "{\n"
        + "characterName: " + characterName + ",\n"
        + "characterClass: " + characterClass + "\n"
        + "}";
  }
}

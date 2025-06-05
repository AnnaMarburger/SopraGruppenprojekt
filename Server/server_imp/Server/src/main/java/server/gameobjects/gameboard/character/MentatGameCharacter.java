package server.gameobjects.gameboard.character;

import shared_data.Position;
import shared_data.character.CharacterType;
import shared_data.config.PartyConfig;

/**
 * This class represents a mentat game character.
 * Used for in game data saving.
 */
public class MentatGameCharacter extends GameCharacter {

  public MentatGameCharacter(Position position, String name) {
    super(CharacterType.MENTAT, position, name,
        PartyConfig.getCurrentConfig().mentat.maxHP,
        PartyConfig.getCurrentConfig().mentat.maxMP,
        PartyConfig.getCurrentConfig().mentat.maxAP,
        PartyConfig.getCurrentConfig().mentat.damage,
        PartyConfig.getCurrentConfig().mentat.inventorySize,
        PartyConfig.getCurrentConfig().mentat.healingHP);
  }
}

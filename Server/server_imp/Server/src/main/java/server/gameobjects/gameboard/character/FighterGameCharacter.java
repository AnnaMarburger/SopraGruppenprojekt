package server.gameobjects.gameboard.character;

import shared_data.Position;
import shared_data.character.CharacterType;
import shared_data.config.PartyConfig;

/**
 * This class represents a fighter game character.
 * Used for in game data saving.
 */
public class FighterGameCharacter extends GameCharacter {

  public FighterGameCharacter(Position position, String name) {
    super(CharacterType.FIGHTER, position, name,
        PartyConfig.getCurrentConfig().fighter.maxHP,
        PartyConfig.getCurrentConfig().fighter.maxMP,
        PartyConfig.getCurrentConfig().fighter.maxAP,
        PartyConfig.getCurrentConfig().fighter.damage,
        PartyConfig.getCurrentConfig().fighter.inventorySize,
        PartyConfig.getCurrentConfig().fighter.healingHP);
  }
}

package server.gameobjects.gameboard.character;

import shared_data.Position;
import shared_data.character.CharacterType;
import shared_data.config.PartyConfig;

/**
 * This class represents a noble game character.
 * Used for in game data saving.
 */
public class NobleGameCharacter extends GameCharacter {

  public NobleGameCharacter(Position position, String name) {
    super(CharacterType.NOBLE, position, name,
        PartyConfig.getCurrentConfig().noble.maxHP,
        PartyConfig.getCurrentConfig().noble.maxMP,
        PartyConfig.getCurrentConfig().noble.maxAP,
        PartyConfig.getCurrentConfig().noble.damage,
        PartyConfig.getCurrentConfig().noble.inventorySize,
        PartyConfig.getCurrentConfig().noble.healingHP);
  }
}

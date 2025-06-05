package server.gameobjects.gameboard.character;

import shared_data.Position;
import shared_data.character.CharacterType;
import shared_data.config.PartyConfig;

/**
 * This class represents a bene gesserit game character.
 * Used for in game data saving.
 */
public class BeneGesseritGameCharacter extends GameCharacter {

  public BeneGesseritGameCharacter(Position position, String name) {
    super(CharacterType.BENE_GESSERIT, position, name,
        PartyConfig.getCurrentConfig().beneGesserit.maxHP,
        PartyConfig.getCurrentConfig().beneGesserit.maxMP,
        PartyConfig.getCurrentConfig().beneGesserit.maxAP,
        PartyConfig.getCurrentConfig().beneGesserit.damage,
        PartyConfig.getCurrentConfig().beneGesserit.inventorySize,
        PartyConfig.getCurrentConfig().beneGesserit.healingHP);
  }
}

package server.gameobjects.gameboard.character;

import static shared_data.CONST.CHARACTER_CONFIG_IS_NULL;
import static shared_data.CONST.logger;
import static shared_data.CONST.random;

import messages.data.dictionary.Attributes;
import messages.data.dictionary.Stats;
import server.ServerMain;
import server.gameobjects.Game;
import server.gameobjects.gameboard.GameBoard;
import server.gameobjects.gameboard.GameTileType;
import shared_data.Position;
import shared_data.character.CharacterType;
import shared_data.config.CharacterConfig;
import shared_data.config.PartyConfig;

/**
 * This class represents a controller for an in-game character.
 * Used to obstruct the view to the concrete character data.
 */
public class CharacterController {

  protected GameCharacter gameCharacter;

  private final GameBoard map;

  public CharacterController(GameCharacter character, GameBoard map) {
    this.gameCharacter = character;
    this.map = map;
    map.getTile(getPosition()).setOccupied(true);
  }

  //region Setter
  public void setCloneable(boolean cloneable) {
    if (!cloneable) {
      setIsDead(true);
    }
    this.gameCharacter.cloneable = cloneable;
  }

  public void setSpice(int amount) {
    gameCharacter.currentInventory.setSpiceAmount(amount);
  }

  public void setIsDead(boolean isDead) {
    if (isDead) {
      gameCharacter.dead = true;
      gameCharacter.hp = 0;
      onDeath();
    } else {
      gameCharacter.dead = false;
    }
  }
  //endregion

  //region Getter
  public int getSpice() {
    return gameCharacter.currentInventory.getSpiceAmount();
  }

  public boolean isDead() {
    return gameCharacter.dead;
  }

  public boolean isCloneable() {
    return gameCharacter.cloneable;
  }

  public Position getPosition() {
    return gameCharacter.position;
  }

  public int getCharacterID() {
    return gameCharacter.id;
  }

  public String getCharacterName() { return gameCharacter.name; }

  public int getAP() {
    return gameCharacter.ap;
  }

  public int getHP() { return gameCharacter.hp; }

  public int getDamageAmount() {
    return gameCharacter.damage;
  }

  public boolean cannotDoNormalAction() {
    return gameCharacter.ap < 1;
  }

  public boolean cannotDoSpecialAction() {
    var config = getCharacterConfig();
    if (config == null) {
      logger.severe(CHARACTER_CONFIG_IS_NULL);
      ServerMain.closeServer();
      return true;
    }
    return gameCharacter.ap != config.maxAP;
  }

  public int getAvailableInventorySpace() {
    return gameCharacter.inventorySize - gameCharacter.currentInventory.getSpiceAmount();
  }

  public int getMovementPoints() {
    return gameCharacter.mp;
  }

  public boolean isTurnFinished() {
    return gameCharacter.ap == 0 && gameCharacter.mp == 0;
  }

  public Stats getStats() {
    return gameCharacter.getStats();
  }

  public Attributes getAttributes() {
    var cConfig = getCharacterConfig();

    if (cConfig == null) {
      logger.severe(CHARACTER_CONFIG_IS_NULL);
      ServerMain.closeServer();
      return null;
    }

    return new Attributes(gameCharacter.type, cConfig.maxHP, gameCharacter.hp,
        gameCharacter.healing, cConfig.maxMP, gameCharacter.mp, cConfig.maxAP, gameCharacter.ap,
        gameCharacter.damage, gameCharacter.inventorySize,
        gameCharacter.currentInventory.getSpiceAmount(), !gameCharacter.cloneable,
        gameCharacter.sound);
  }

  public boolean isSound() {
    return gameCharacter.sound;
  }

  public GameCharacter getGameCharacter() {
    return gameCharacter;
  }

  public CharacterType getCharacterType() {
    return gameCharacter.type;
  }

  private CharacterConfig getCharacterConfig() {
    CharacterConfig cConfig = null;
    switch (gameCharacter.type) {
      case NOBLE -> cConfig = PartyConfig.getCurrentConfig().noble;
      case MENTAT -> cConfig = PartyConfig.getCurrentConfig().mentat;
      case BENE_GESSERIT -> cConfig = PartyConfig.getCurrentConfig().beneGesserit;
      case FIGHTER -> cConfig = PartyConfig.getCurrentConfig().fighter;
    }
    return cConfig;
  }
  //endregion

  /**
   * This method attacks the given character (character controller) on the specified position.
   * Checks if high ground or low ground bonus or malus has to be calculated into the attack damage.
   *
   * @param targetCharacterController character that is being attacked
   * @param target position of the attacked character
   */
  public void attack(CharacterController targetCharacterController, Position target) {
    var applyBonus = false;
    var applyMalus = false;
    if (Game.getGameInstance().getHeightForPosition(this.getPosition()) > Game.getGameInstance()
        .getHeightForPosition(target)) {
      applyBonus = true;
    } else if (Game.getGameInstance().getHeightForPosition(this.getPosition())
        < Game.getGameInstance()
        .getHeightForPosition(target)) {
      applyMalus = true;
    }
    if (applyBonus) {
      targetCharacterController.decreaseHP(Math.round(
          PartyConfig.getCurrentConfig().highGroundBonusRatio * this.getDamageAmount()));
    } else if (applyMalus) {
      targetCharacterController.decreaseHP(
          Math.round(PartyConfig.getCurrentConfig().lowGroundMalusRatio * this.getDamageAmount()));
    } else {
      targetCharacterController.decreaseHP(this.getDamageAmount());
    }

    decreaseAP(1);
  }

  /**
   * This method represents a kanly attack.
   * If the kanly success probability is high enough the character is killed immediately.
   */
  public void kanly() {
    if (random.nextFloat() >= PartyConfig.getCurrentConfig().kanlySuccessProbability) {
      gameCharacter.hp = 0;
      setIsDead(true);
    }
  }

  /**
   * This method spawns the character at the specified position. Also sets the tile to occupied.
   *
   * @param pos position of the tile the character shall be spawned on
   */
  public void spawnAt(Position pos) {
    gameCharacter.position.copyPosition(pos);
    map.getTile(getPosition()).setOccupied(true);
  }

  /**
   * This method moves the character to the specified position by calling the method moved(). Makes
   * the player loud if walking on sand. Reduces mp.
   *
   * @param newPosition specified position the character is walking on to
   * @param type type of game tile the player is walking on to
   */
  public void move(Position newPosition, GameTileType type) {
    if (type == GameTileType.FLAT_SAND || type == GameTileType.DUNE) {
      desertWalk();
    }
    map.getTile(getPosition()).setOccupied(false);
    moved(newPosition);
    gameCharacter.mp -= 1;
  }

  /**
   * This method moves the player on to the new position. Sets tile that the character is moving
   * on to occupied.
   *
   * @param newPosition specified position the character is moving on.
   */
  public void moved(Position newPosition) {
    gameCharacter.position.copyPosition(newPosition);
    map.getTile(getPosition()).setOccupied(true);
  }

  /**
   * This method decreases the hp of the character by the given amount. Checks if the player is
   * dead.
   *
   * @param damageAmount amount of damage
   */
  public void decreaseHP(int damageAmount) {
    if (gameCharacter.hp > damageAmount) {
      gameCharacter.hp = gameCharacter.hp - damageAmount;
    } else {
      gameCharacter.hp = 0;
      setIsDead(true);
    }
  }

  /**
   * This method sets the player to loud if the character moved 2 times on sand.
   */
  private void desertWalk() {
    gameCharacter.currSound++;
    if (gameCharacter.currSound > 1) {
      gameCharacter.sound = true;
    }
  }

  /**
   * This decreases the ap.
   *
   * @param amount amount of ap to decrease
   */
  public void decreaseAP(int amount) {
    gameCharacter.ap -= amount;
  }

  public void decreaseMP(int amount) { gameCharacter.mp -= amount; }

  /**
   * This method tries to heal the character. Only heals if the player did not move this turn and
   * is not on full hp.
   *
   * @return true if healed, else false
   */
  public boolean tryHeal() {
    var config = getCharacterConfig();
    if (config == null) {
      logger.severe(CHARACTER_CONFIG_IS_NULL);
      ServerMain.closeServer();
      return false;
    }

    if (gameCharacter.mp == config.maxMP && gameCharacter.hp != config.maxHP) {
      this.gameCharacter.hp = this.gameCharacter.hp + this.gameCharacter.healing;
      return true;
    }
    return false;
  }

  /**
   * This method resets the character. Calls the method resetStats() to reset the stats.
   *
   * @return true if successful, else false
   */
  public boolean resetCharacter() {
    var config = getCharacterConfig();
    if (config == null) {
      logger.severe(CHARACTER_CONFIG_IS_NULL);
      ServerMain.closeServer();
      return false;
    }
    return resetStats(config);
  }

  /**
   * This method resets the stats of the character.
   *
   * @param config config the stats shall be read from
   * @return true if successful, else false
   */
  private boolean resetStats(CharacterConfig config) {
    var statChanged = false;
    if (!gameCharacter.cloneable) {
      return false;
    }
    if (gameCharacter.dead) {
      setIsDead(false);
      gameCharacter.currentInventory.setSpiceAmount(0);
      statChanged = true;
    }
    if(gameCharacter.ap != config.maxAP ||
        gameCharacter.mp != config.maxMP||
        gameCharacter.sound
    ){
      gameCharacter.sound = false;
      gameCharacter.currSound = 0;
      gameCharacter.ap = config.maxAP;
      gameCharacter.mp = config.maxMP;
      statChanged = true;
    }
    return statChanged;
  }

  /**
   * This method sets current tile the character stood on to un-occupied. Calls spiceSpread() and
   * sets spice of player to 0.
   */
  private void onDeath() {
    map.getTile(getPosition()).setOccupied(false);
    map.spiceSpread(getSpice(), getPosition());
    setSpice(0);
  }
}


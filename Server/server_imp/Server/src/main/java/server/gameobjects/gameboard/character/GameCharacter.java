package server.gameobjects.gameboard.character;

import messages.data.dictionary.Stats;
import server.ServerIDs;
import server.gameobjects.gameboard.Inventory;
import shared_data.Position;
import shared_data.character.CharacterType;

/**
 * This class represents an abstract game character. Holds all the data.
 */
public abstract class GameCharacter {

  protected final int id;
  protected final CharacterType type;
  protected final Position position;
  protected final String name;
  protected int hp;
  protected int mp;
  protected int ap;
  protected final int damage;
  protected int inventorySize;
  protected final Inventory currentInventory;
  protected final int healing;
  protected boolean sound;
  protected boolean cloneable; //inverted isSwallowed
  protected boolean dead;
  protected int currSound;

  protected GameCharacter(CharacterType type, Position position, String name, int hp, int mp,
      int ap, int damage, int inventorySize, int healing) {
    this.id = ServerIDs.getInstance().getCharacterID();
    this.type = type;
    this.position = position;
    this.name = name;
    this.hp = hp;
    this.mp = mp;
    this.ap = ap;
    this.damage = damage;
    this.inventorySize = inventorySize;
    this.healing = healing;

    this.currentInventory = new Inventory();
    this.sound = false;
    this.cloneable = true;
    this.dead = false;
    this.currSound = 0;
  }

  public int getId() {
    return id;
  }

  public Position getPosition() {
    return position;
  }

  /**
   * This method constructs and then returns stats for the character.
   *
   * @return current stats of the character
   */
  public Stats getStats() {
    return new Stats(hp, ap, mp,
        currentInventory.getSpiceAmount(), sound,
        !cloneable);
  }


}

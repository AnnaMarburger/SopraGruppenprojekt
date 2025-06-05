package server.gameobjects;

import java.util.ArrayList;
import java.util.List;
import server.ServerIDs;
import server.gameobjects.gameboard.character.CharacterController;
import shared_data.Position;
import shared_data.house.HouseName;

/**
 * This class represents a player in the game.
 */
public class Player {

  public final String playerName;
  private final boolean cpu;
  private int clientID;
  private final int playerNumber;
  private Position cityPosition;
  private HouseName houseName;

  private int spice;
  private int spicePickupCounter;
  private int enemiesKilledCounter;
  private int charactersEatenCounter;
  private List<CharacterController> characterControllers;

  private boolean shunned;

  private int atomicsCounter = 3;

  public Player(String playerName, boolean cpu) {
    this.playerName = playerName;
    this.playerNumber = ServerIDs.getInstance().getPlayerNumber();
    this.spice = 0;
    this.shunned = false;
    this.cpu = cpu;
  }

  //region Getter
  public HouseName getHouseName() {
    return houseName;
  }

  public List<CharacterController> getCharacterControllers() {
    return characterControllers;
  }

  public int getPlayerNumber() {
    return playerNumber;
  }

  public int getClientID() {
    return clientID;
  }

  public Position getCityPosition() {
    return cityPosition;
  }

  public List<CharacterController> getCloneableCharacters() {
    List<CharacterController> cloneable = new ArrayList<>();
    for (CharacterController c : characterControllers) {
      if (c.isCloneable()) {
        cloneable.add(c);
      }
    }
    return cloneable;
  }

  public int getSpice() {
    return spice;
  }

  public int getSpicePickupCounter() {
    return spicePickupCounter;
  }

  public int getEnemiesKilledCounter() {
    return enemiesKilledCounter;
  }

  public boolean isShunned() {
    return shunned;
  }

  public int getCharactersEatenCounter() {
    return charactersEatenCounter;
  }

  public int getAtomicsCounter() {
    return atomicsCounter;
  }
  //endregion

  //region Setter
  public void setHouseName(HouseName houseName) {
    this.houseName = houseName;
  }


  public void setCityPosition(Position position) {
    cityPosition = position;
  }

  public void setClientID(int clientID) {
    this.clientID = clientID;
  }

  public void setCharacterControllers(List<CharacterController> characterControllers) {
    this.characterControllers = characterControllers;
  }

  public void setCharactersEatenCounter(int charactersEatenCounter) {
    this.charactersEatenCounter = charactersEatenCounter;
  }

  public void setSpicePickupCounter(int spicePickupCounter) {
    this.spicePickupCounter = spicePickupCounter;
  }

  public void setSpice(int spice) {
    this.spice = spice;
  }

  public void setShunned() {
    this.shunned = true;
  }

  public void setEnemiesKilledCounter(int enemiesKilledCounter) {
    this.enemiesKilledCounter = enemiesKilledCounter;
  }

  public void useAtomics() {
    atomicsCounter--;
  }
  //endregion

  /**
   * This method checks if the player has at least one character on the map.
   *
   * @return true if at least on own character is on the map, else false
   */
  public boolean checkIfCharactersOnMap() {
    for (CharacterController currCharacter : characterControllers) {
      if (!currCharacter.isDead() && currCharacter.isCloneable()) {
        return true;
      }
    }
    return false;
  }

  public boolean isCpu() {
    return cpu;
  }
}
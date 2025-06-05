package server.gameobjects;

import server.gameobjects.gameboard.character.GameCharacter;
import shared_data.Position;

/**
 * This class represents a worm. Holds all the data.
 */
public class Worm {

  Position pos;
  boolean active;

  GameCharacter target;

  Integer targetClientID;

  public Worm() {
    active = false;
    pos = new Position(0, 0);
    target = null;
    targetClientID = null;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public GameCharacter getTarget() {
    return target;
  }

  public void setTarget(GameCharacter target, int targetClientID) {

    this.target = target;
    this.targetClientID = targetClientID;
  }

  public Position getPos() {
    return pos;
  }
}

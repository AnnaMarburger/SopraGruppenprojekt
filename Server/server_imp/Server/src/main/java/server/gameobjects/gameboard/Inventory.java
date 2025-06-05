package server.gameobjects.gameboard;

/**
 * This class represents the inventory for a character. Saves amount of spice.
 */
public class Inventory {

  private int spiceAmount;

  public Inventory() {
    this.spiceAmount = 0;
  }

  public int getSpiceAmount() {
    return this.spiceAmount;
  }

  public void setSpiceAmount(int newSpiceAmount) {
    this.spiceAmount = newSpiceAmount;
  }
}

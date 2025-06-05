package server.gameobjects.gameboard;

/**
 * This class represents a tile in the game data format.
 */
public enum GameTileType {

  CITY("CITY", false, 1),
  FLAT_SAND("FLAT_SAND", true, 0),
  FLAT_SAND_WORM("FLAT_SAND", false, 0),
  DUNE("DUNE", true, 1),
  DUNE_WORM("DUNE", false, 1),

  MOUNTAINS("MOUNTAINS", false, 1),
  PLATEAU("PLATEAU", true, 0),
  HELIPORT("HELIPORT",true,0);


  public final int height; // 0 if low, 1 if high
  public final String typeName; // type name
  public final boolean accessible; // true if accessible, else false

  GameTileType(String typeName, boolean accessible, int height) {
    this.height = height;
    this.typeName = typeName;
    this.accessible = accessible;
  }

  @Override
  public String toString() {
    return "TileType{" +
        "height=" + height +
        ", typeName='" + typeName + '\'' +
        ", accessible=" + accessible +
        '}';
  }
}

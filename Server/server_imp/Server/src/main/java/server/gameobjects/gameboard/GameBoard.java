package server.gameobjects.gameboard;

import static shared_data.CONST.logger;
import static shared_data.CONST.random;

import java.util.*;

import java.util.function.BiFunction;
import messages.MapChangeDemandMessage;
import messages.data.ChangeReason;
import server.MessageHandler;
import server.gameobjects.gameboard.dijkstra.*;
import messages.data.tile.MessageTile;
import shared_data.Position;
import shared_data.config.PartyConfig;

/**
 * This class represents the game board.
 */
public class GameBoard {

  private GameTile[][] map;
  private final int vIndex;
  private final int hIndex;
  private final Position eyeOfTheStorm;

  public GameBoard(int vIndex, int hIndex, GameTile[][] map) {
    this.vIndex = vIndex;
    this.hIndex = hIndex;
    this.map = map;
    this.eyeOfTheStorm = new Position(random.nextInt(hIndex), random.nextInt(vIndex));
    generateStorm();
  }

  public Position randomAccessiblePosition(){
    GameTile tempTile = map[random.nextInt(hIndex-1)][random.nextInt(vIndex-1)];
    if (!tempTile.isOccupied() && tempTile.getAccessibility()){
      return tempTile.getPosition();
    }
    else {
      return randomAccessiblePosition();
    }
  }

  //region Spice Blow
  public void spiceSpread(int amount, Position pos) {
    if(amount == 0)
      return;
    spiceSpreadRecursion(amount,pos);

    MessageHandler.getInstance().addMessageToQueue(
        new MapChangeDemandMessage(ChangeReason.ROUND_PHASE, convertMapToMessageFormat(),
            eyeOfTheStorm));
  }

  private void spiceSpreadRecursion(int amount, Position pos) {
    if (amount == 0) {
      return;
    }
    if (!getTile(pos).isSpice()) {
      amount--;
      getTile(pos).addSpice();
    }
    List<GameTile> tiles = getTilesInRadiusWithMinDistance(1, pos, 0);
    int finalAmount = amount;
    getRandomTile(tiles, (y, x) -> getTile(new Position(x, y)).getAccessibility()).ifPresentOrElse(
        x -> spiceSpreadRecursion(finalAmount, x.getPosition()),
        () -> logger.info(
            "Spice Spread couldn't spread, because there are to no accessible tiles.")
    );
  }

  public void checkSpiceBlow() {
    if (PartyConfig.getCurrentConfig().spiceMinimum > getMapSpiceAmount()) {
      int amount = 3 + random.nextInt(4);
      getRandomTile(twoDArrayToList(map), this::isTraversableForWorm).ifPresentOrElse(
          x -> {
            spiceExplosion(x);
            spiceSpread(amount, x.getPosition());
          },
          () -> {
          }
      );

    }
  }

  private void spiceExplosion(GameTile tile) {
    List<GameTile> tiles = getTilesInRadiusWithMinDistance(1, tile.getPosition(), 0);
    tiles.add(tile);
    for (GameTile curTile : tiles
    ) {
      if (isDesertTile(curTile)) {
        if (random.nextInt(2) == 0) {
          curTile.changeType(GameTileType.DUNE);
        } else {
          curTile.changeType(GameTileType.FLAT_SAND);
        }
      }
    }
    MessageHandler.getInstance().addMessageToQueue(
        new MapChangeDemandMessage(ChangeReason.ROUND_PHASE, convertMapToMessageFormat(),
            eyeOfTheStorm));
  }

  public boolean isDesertTile(GameTile tile) {
    return tile.getTileType() == GameTileType.DUNE || tile.getTileType() == GameTileType.FLAT_SAND
        || tile.getTileType() == GameTileType.DUNE_WORM
        || tile.getTileType() == GameTileType.FLAT_SAND_WORM;
  }
  //endregion

  //region Sandworm
  public boolean isTraversableForWorm(int y, int x) {
    return (isOnMap(y, x) &&
        (map[y][x].getTileType() == GameTileType.DUNE ||
            map[y][x].getTileType() == GameTileType.FLAT_SAND));
  }


  public boolean isValidSpawnForWorm(int y, int x) {
    return (isTraversableForWorm(y, x) && !map[y][x].isOccupied());
  }
  //endregion

  //region Dune Walk
  public void duneWalk() {
    var newMap = new GameTile[vIndex][hIndex];

    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        newMap[y][x] = map[y][x];
      }
      }
    // Loop through every cell
    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        // finding no Of Neighbours that are alive

        var aliveNeighbours = 0;
        for (int yy = -1; yy <= 1; yy++) {
          for (int xx = -1; xx <= 1; xx++) {
            if (xx == 0 && yy == 0) {
              continue; // You are not neighbor to yourself
            }
            if (isOnMap(y + yy, x + xx)) {
              aliveNeighbours += isAlive(y + yy, x + xx);
            }
          }
        }

        // Implementing the Rules of Life
        if (isAlive(y, x) == 0) {

          //cell can be born

            if (PartyConfig.getCurrentConfig().caBorn.contains(aliveNeighbours)) {
              //cell is born
              if (map[y][x].getHeight()==0&&map[y][x].getTileType()==GameTileType.FLAT_SAND)
                newMap[y][x] = resurrectTile(map[y][x]);

            }




        } else {


            if (!PartyConfig.getCurrentConfig().caSurvive.contains(aliveNeighbours)) {
              //cell survives
              if(map[y][x].getHeight()==1&&map[y][x].getTileType()==GameTileType.DUNE)
              newMap[y][x] = killTile(map[y][x]);

            }




        }
      }
    }
    map=newMap;
  }

  private GameTile killTile(GameTile t) {
    return new GameTile(t.getPosition(), GameTileType.FLAT_SAND, t.isOccupied(), t.isSpice(),
        t.isHasSandstorm());
  }

  private GameTile resurrectTile(GameTile t) {
    return new GameTile(t.getPosition(), GameTileType.DUNE, t.isOccupied(), t.isSpice(),
        t.isHasSandstorm());
  }

  private int isAlive(int y, int x) {
    return map[y][x].getHeight();
  }
  //endregion

  //region Sandstorm
  private void clearStorm() {
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        if (isOnMap(y + eyeOfTheStorm.y, x + eyeOfTheStorm.x)) {
          map[y + eyeOfTheStorm.y][x + eyeOfTheStorm.x].setHasSandstorm(false);
        }
      }
    }
  }

  private void generateStorm() {
    for (int y = -1; y <= 1; y++) {
      for (int x = -1; x <= 1; x++) {
        if (isOnMap(y + eyeOfTheStorm.y, x + eyeOfTheStorm.x)) {
          map[y + eyeOfTheStorm.y][x + eyeOfTheStorm.x].setHasSandstorm(true);
        }
      }
    }
  }

  public void moveSandStorm() {
    clearStorm();
    eyeOfTheStorm.moveOneRandom();
    generateStorm();
  }
  //endregion

  //region Getter
  public GameTile[][] getMap() {
    return map;
  }

  private boolean isOnMap(int y, int x) {
    return x >= 0 && y >= 0 && x < hIndex && y < vIndex;
  }

  private int getMapSpiceAmount() {
    var amount = 0;
    for (var y = 0; y < hIndex; y++) {
      for (var x = 0; x < vIndex; x++) {
        if (map[y][x].isSpice()) {
          amount++;
        }
      }
    }
    return amount;
  }

  public Optional<GameTile> getRandomTile(List<GameTile> tiles,
      BiFunction<Integer, Integer, Boolean> function) {
    Collections.shuffle(tiles);
    GameTile tile = null;
    for (GameTile gameTile : tiles) {
      if (Boolean.TRUE.equals(function.apply(gameTile.getPosition().y, gameTile.getPosition().x))) {
        tile = gameTile;
        break;
      }
    }
    return Optional.ofNullable(tile);
  }

  public List<GameTile> getTilesInRadiusWithMinDistance(int radius, Position pos, int minDistance) {
    List<GameTile> tiles;
    tiles = convertNodesToAccessibleGameTiles(
        MapAlgorithms.getRadiusWithMinDistance(vIndex, hIndex, pos, radius, minDistance));
    return tiles;
  }

  public GameTile getTile(Position pos) {
    if (pos.x < hIndex && pos.y < vIndex) {
      return map[pos.y][pos.x];
    }
    return null;
  }

  public Position getEyeOfTheStorm() {
    return eyeOfTheStorm;
  }
  //endregion

  public List<Position> findCityPositions() {
    List<Position> cityPositions = new ArrayList<>();

    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        if (map[y][x].getTileType() == GameTileType.CITY) {
          cityPositions.add(new Position(x, y));
        }
      }
    }

    return cityPositions;
  }

  public MessageTile[][] convertMapToMessageFormat() {
    var messageMap = new MessageTile[vIndex][hIndex];
    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        messageMap[y][x] = map[y][x].toMessageTile();
      }
    }
    return messageMap;
  }

  public void assignCityTileToClientID(Position pos, int clientID) {
    map[pos.y][pos.x].setClientID(clientID);
  }

  private List<GameTile> convertNodesToAccessibleGameTiles(List<Node> nodes) {
    List<GameTile> tiles = new ArrayList<>();
    GameTile tmpTile;
    for (Node n : nodes
    ) {
      tmpTile = getTile(n.position);
      if (tmpTile.getAccessibility() && !tiles.contains(tmpTile)) {
        tiles.add(tmpTile);
      }
    }
    return tiles;
  }

  public char[][] convertToCharMapForWorm() {
    var charMap = new char[vIndex][hIndex];
    for (var y = 0; y < vIndex; y++) {
      for (var x = 0; x < hIndex; x++) {
        charMap[y][x] = isTraversableForWorm(y, x) ? '1' : '0';
      }
    }
    return charMap;
  }

  public void changeToEndgameMap() {
    for (var y = 0; y < hIndex; y++) {
      for (var x = 0; x < vIndex; x++) {
        System.out.println("MAP CHANGE ENDGAME");
        if (map[y][x].getTileType().equals(GameTileType.MOUNTAINS) ||
            map[y][x].getTileType().equals(GameTileType.PLATEAU)) {
          System.out.println("CHANGING TILE");
          map[y][x].changeType(GameTileType.DUNE);
        }
      }
    }
  }

  public <T> List<T> twoDArrayToList(T[][] twoDArray) {
    List<T> list = new ArrayList<>();
    for (T[] array : twoDArray) {
      list.addAll(Arrays.asList(array));
    }
    return list;
  }
}

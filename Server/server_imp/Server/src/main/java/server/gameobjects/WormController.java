package server.gameobjects;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import messages.MapChangeDemandMessage;
import messages.SandwormDespawnDemandMessage;
import messages.SandwormMoveDemandMessage;
import messages.SandwormSpawnDemandMessage;
import messages.data.ChangeReason;
import server.MessageHandler;
import server.gameobjects.gameboard.dijkstra.MapAlgorithms;
import server.gameobjects.gameboard.GameBoard;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.GameTileType;
import server.gameobjects.gameboard.character.GameCharacter;
import shared_data.CONST;
import shared_data.Position;
import shared_data.config.PartyConfig;

/**
 * This class represents a worm controller. It obstructs the direct view to the worm  data.
 */
public class WormController {

  private final Worm worm;

  private final GameBoard map;

  private final int wormSpawnRadius;

  private final int wormSpeed;

  public WormController(GameBoard map) {
    worm = new Worm();
    this.map = map;
    if (PartyConfig.getCurrentConfig() != null) {
      this.wormSpawnRadius = PartyConfig.getCurrentConfig().sandWormSpawnDistance;
      this.wormSpeed = PartyConfig.getCurrentConfig().sandWormSpeed;
    } else {
      this.wormSpeed = 2;
      this.wormSpawnRadius = 2;
      CONST.logger.severe("COULDN'T LOAD CONFIG. LOADED BASE VALUE FORM WORM!");
    }
  }

  //region Getter
  public boolean isActive() {
    return worm.isActive();
  }

  public GameCharacter getTarget() {
    return worm.getTarget();
  }

  public Position getPosition() {
    return worm.getPos();
  }

  public boolean canEat() {
    return map.getTile(worm.pos).isOccupied();
  }

  public int getWormSpawnRadius() {
    return wormSpawnRadius;
  }

  public int getTargetClientID(){
    return worm.targetClientID;
  }
  //endregion

  /**
   * This method starts the hunt of the worm. Sets the target of the worm and activates the worm.
   *
   * @param target targeted character
   * @param clientID client id of the targeted character
   * @param wormPos position of the worm spawn
   */
  public void startHunt(GameCharacter target, int clientID, Position wormPos) {
    worm.setTarget(target, clientID);
    worm.pos.copyPosition(wormPos);
    if(!this.isActive())
      changeTileToWormTile(map.getTile(wormPos));
      MessageHandler.getInstance().addMessageToQueue(
             new SandwormSpawnDemandMessage(clientID,
                     target.getId(),
                      this.getPosition()));
    worm.setActive(true);

    hunt();
  }

  /**
   * This method hunts the targeted character.
   *
   * @return List of Positions - represents walking path
   */
  public List<Position> hunt() {
    if (checkIfTargetLost()) {
      despawnWorm();
      return null;
    }
    List<Position> allPositions = MapAlgorithms.shortestPath(
        worm.getPos(), worm.target.getPosition(), map.convertToCharMapForWorm());
    if (!allPositions.isEmpty()) {
      Collections.reverse(allPositions);
      var walkPositions = new Stack<Position>();
      Position currPos;
      for (var i = 0; i < Math.min(wormSpeed, allPositions.size()); i++) {
        currPos = allPositions.get(i);
        map.getTile(currPos).changeType(GameTileType.FLAT_SAND);
        walkPositions.push(allPositions.get(i));
      }

      MessageHandler.getInstance().addMessageToQueue(new SandwormMoveDemandMessage(walkPositions));
      moveWorm(walkPositions.peek());
      return walkPositions;
    }


    return allPositions;
  }

  /**
   * This method changes the given tile to a worm tile.
   *
   * @param tile tile to change
   */
  private void changeTileToWormTile(GameTile tile) {
    switch (tile.getTileType()) {
      case DUNE -> tile.changeType(GameTileType.DUNE_WORM);
      case FLAT_SAND -> tile.changeType(GameTileType.FLAT_SAND_WORM);
      default -> CONST.logger.severe("WORM IS ON WRONG TILE!");
    }
  }

  /**
   * This method changes the given tile from a worm tile into a normal tile.
   *
   * @param tile tile to change
   */
  private void reverseFromWormTile(GameTile tile) {
    switch (tile.getTileType()) {
      case DUNE_WORM -> tile.changeType(GameTileType.DUNE);
      case FLAT_SAND_WORM -> tile.changeType(GameTileType.FLAT_SAND);
      default -> CONST.logger.severe("FORGOT TO SET WORM TILE OR WORM WAS ON WRONG TILE!");
    }
  }

  /**
   * This method moves the worm. Changes everything accordingly.
   *
   * @param pos position the worm is moving to
   */
  private void moveWorm(Position pos) {
    reverseFromWormTile(map.getTile(worm.pos));
    changeTileToWormTile(map.getTile(pos));
    worm.pos.copyPosition(pos);
    if(canEat()){
      Game.getGameInstance().getPhaseHandler().getWormPhaseHandler().wormEat();
    }
    MessageHandler.getInstance().addMessageToQueue(
        new MapChangeDemandMessage(ChangeReason.ROUND_PHASE, map.convertMapToMessageFormat(),
            map.getEyeOfTheStorm()));
  }

  /**
   * This method de-spawns the worm. Deactivated the worm.
   */
  public void despawnWorm() {
    reverseFromWormTile(map.getTile(worm.pos));
    worm.setActive(false);
    MessageHandler.getInstance().addMessageToQueue(new SandwormDespawnDemandMessage());
  }

  /**
   * This method checks if the targeted character doesn't stand on a sand tile.
   *
   * @return true if character still stands on a sand tile, else false
   */
  private boolean checkIfTargetLost() {
    return map.getTile(worm.target.getPosition()).getTileType() == GameTileType.PLATEAU || map.getTile(worm.target.getPosition()).getTileType() == GameTileType.HELIPORT;
  }
}

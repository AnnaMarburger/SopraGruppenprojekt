package server.gameobjects.gameboard;

import messages.data.tile.MessageTile;
import messages.data.tile.MessageTileType;
import shared_data.Position;

/**
 * This class represents a game tile in the game data format.
 */
public class GameTile {

    private final Position pos;
    private GameTileType gameTileType;
    private boolean occupied;
    private boolean spice;

    private Integer clientID;

    private boolean hasSandstorm;

    public GameTile(Position pos, GameTileType gameTileType, boolean occupied, boolean spice,
                    boolean hasSandstorm) {
        this.pos = pos;
        this.gameTileType = gameTileType;
        this.occupied = occupied;
        this.spice = spice;
        this.hasSandstorm = hasSandstorm;
        this.clientID = null;
    }

    public GameTile(Position pos, GameTileType gameTileType) {
        this.pos = pos;
        this.gameTileType = gameTileType;
        this.occupied = false;
        this.spice = false;
        this.hasSandstorm = false;
        this.clientID = null;
    }

    //region Setter
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setHasSandstorm(boolean hasSandstorm) {
        this.hasSandstorm = hasSandstorm;
    }

    public void removeSpice() {
        this.spice = false;
    }

    public void addSpice() {
        this.spice = true;
    }
    //endregion

    //region Getter

    public Position getPosition() {
        return pos;
    }

    public boolean isSpice() {
        return spice;
    }

    public int getClientID() {
        return clientID;
    }

    public boolean isHasSandstorm() {
        return hasSandstorm;
    }

    public int getHeight() {
        return gameTileType.height;
    }

    public boolean getAccessibility() {
        return gameTileType.accessible;
    }

    public GameTileType getTileType() {
        return gameTileType;
    }

    public boolean isOccupied() {
        return occupied;
    }

    //endregion

    /**
     * This method changes the type of the tile to the given type. Conserves the sand worm on the
     * tile.
     *
     * @param gameTileType type to change to
     */
    public void changeType(GameTileType gameTileType) {
        if (gameTileType == GameTileType.DUNE) {
            if (this.gameTileType == GameTileType.FLAT_SAND_WORM) {
                this.gameTileType = GameTileType.DUNE_WORM;
            }
            else{
              this.gameTileType = GameTileType.DUNE;
            }
            return;
        } else if (gameTileType == GameTileType.FLAT_SAND) {
            if (this.gameTileType == GameTileType.DUNE_WORM) {
                this.gameTileType = GameTileType.FLAT_SAND_WORM;
            }
            else {
              this.gameTileType = GameTileType.FLAT_SAND;
            }
            return;
        }
        this.gameTileType = gameTileType;
    }

    /**
     * This method turn the tile from the game data format to the message format.
     *
     * @return tile in the message format
     */
    public MessageTile toMessageTile() {
        return new MessageTile(MessageTileType.valueOf(gameTileType.typeName), clientID, spice,
                hasSandstorm);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "pos=" + pos +
                ", tileType=" + gameTileType +
                ", occupied=" + occupied +
                ", spice=" + spice +
                '}';
    }
}

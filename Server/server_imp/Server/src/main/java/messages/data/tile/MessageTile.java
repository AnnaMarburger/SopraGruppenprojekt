package messages.data.tile;

/**
 * This class represents a tile in the message format.
 */
public class MessageTile {

  public final MessageTileType messageTileType;
  public final Integer clientID;
  public final boolean hasSpice;
  public final boolean isInSandstorm;

  public MessageTile(final MessageTileType messageTileType, final Integer clientID,
      final boolean hasSpice,
      final boolean isInSandstorm) {
    this.messageTileType = messageTileType;
    this.clientID = clientID;
    this.hasSpice = hasSpice;
    this.isInSandstorm = isInSandstorm;
  }
}

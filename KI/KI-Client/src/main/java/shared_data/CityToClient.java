package shared_data;

/**
 * This class maps a position to a client id.
 */
public class CityToClient {

  public final int clientID;
  public final int x;
  public final int y;

  public CityToClient(final int clientID, Position pos) {
    this.clientID = clientID;
    this.x = pos.x;
    this.y = pos.y;
  }
}

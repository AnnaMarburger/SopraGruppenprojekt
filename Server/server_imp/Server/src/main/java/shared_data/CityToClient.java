package shared_data;

/**
 * This class maps a position to a client id.
 */
public class CityToClient {

  public final int clientID;
  public final String clientName;
  public final int x;
  public final int y;

  public CityToClient(final int clientID, String clientName, Position pos) {
    this.clientID = clientID;
    this.clientName = clientName;
    this.x = pos.x;
    this.y = pos.y;
  }

  @Override
  public String toString() {
    return ("playerName =" + clientName);
  }
}

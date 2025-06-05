package server;

import org.java_websocket.WebSocket;
import server.gameobjects.Player;
import shared_data.config.PartyConfig;

/**
 * This class represents a client in the game.
 */
public class Client {

  private WebSocket clientWebSocket;
  private final String clientSecret;
  private final Player player;
  private final int clientID;
  private final boolean isCPU;
  private final String clientName;

  private int strikeCounter = 0;


  public Client(String clientSecret, String clientName, WebSocket s, Player player, boolean isCPU) {
    this.clientSecret = clientSecret;
    this.clientID = ServerIDs.getInstance().getClientID();
    this.clientName = clientName;
    this.clientWebSocket = s;
    this.player = player;
    this.isCPU = isCPU;

    if (player != null) {
      player.setClientID(this.clientID);
    }
  }

  public Client(String clientSecret, String clientName, WebSocket s, boolean isCPU) {
    this(clientSecret, clientName, s, null, isCPU);
  }

  //region Getter
  public String getClientSecret() {
    return clientSecret;
  }

  public int getStrikeCount() {
    return strikeCounter;
  }

  public WebSocket getClientWebSocket() {
    return clientWebSocket;
  }

  public Player getPlayer() {
    return player;
  }

  public int getClientID() {
    return clientID;
  }

  public String getClientName(){return clientName;}
  //endregion

  //region Setter
  public void setClientWebSocket(WebSocket s) {
    clientWebSocket = s;
  }
  //endregion

  /**
   * This method strikes the client and increases the strike counter.
   *
   * @return false, if strike limit has been reached, else true
   */
  public boolean strike() {
    if (strikeCounter < PartyConfig.getCurrentConfig().maxStrikes - 1) {
      strikeCounter++;
      return true;
    }
    strikeCounter++;
    return false;
  }
}

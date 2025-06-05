package server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class stores all different kinds of id's the server is awarding.
 */
public class ServerIDs {

  private final AtomicInteger characterID;
  private final AtomicInteger clientID;

  private final AtomicInteger playerNumber;

  private static final ServerIDs server = new ServerIDs();

  public static ServerIDs getInstance() {
    return server;
  }

  private ServerIDs() {
    characterID = new AtomicInteger(0);
    clientID = new AtomicInteger(0);
    playerNumber = new AtomicInteger(0);
  }

  public int getCharacterID() {
    return characterID.getAndIncrement();
  }

  public int getClientID() {
    return clientID.getAndIncrement();
  }

  public int getPlayerNumber() {
    return playerNumber.getAndIncrement();
  }
}

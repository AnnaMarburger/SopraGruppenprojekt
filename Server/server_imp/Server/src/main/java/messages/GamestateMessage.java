package messages;

import java.util.List;
import messages.data.MessageType;


/**
 * This class represents a game state message.
 */
public class GamestateMessage extends Message {

  public final int clientID; // client id
  public final List<Integer> activelyPlayingIDs; // list of all actively playing id's.
  public final List<String> history; // history

  public GamestateMessage(final int clientID, final List<Integer> activelyPlayingIDs,
      List<String> history) {
    super(MessageType.GAMESTATE);
    this.clientID = clientID;
    this.activelyPlayingIDs = activelyPlayingIDs;
    this.history = history;
  }
}

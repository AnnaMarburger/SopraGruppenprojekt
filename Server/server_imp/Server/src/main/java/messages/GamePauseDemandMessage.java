package messages;

import messages.data.MessageType;

/**
 * This class represents a game pause demand message.
 */
public class GamePauseDemandMessage extends Message {

  public final int requestedByClientID; // client id who requested the pause
  public final boolean pause; // pause

  public GamePauseDemandMessage(final int requestedByClientID, boolean pause) {
    super(MessageType.GAME_PAUSE_DEMAND);
    this.requestedByClientID = requestedByClientID;
    this.pause = pause;
  }
}

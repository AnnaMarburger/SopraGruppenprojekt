package messages;

import messages.data.MessageType;

import java.util.List;

/**
 * This class represents a game end message.
 */
public class GameEndMessage extends Message {

  public final int winnerID; // client id of winner
  public final int loserID; // client id of loser
  public final List<String> statistics; // statistics of the game (can be empty)

  public GameEndMessage(final int winnerId, final int loserId, final List<String> statistics) {
    super(MessageType.GAME_END);
    this.winnerID = winnerId;
    this.loserID = loserId;
    this.statistics = statistics;
  }
}

package messages;

import messages.data.MessageType;
import shared_data.Position;

import java.util.List;

/**
 * This class represents a sand worm move demand message.
 */
public class SandwormMoveDemandMessage extends Message {

  public final List<Position> path; // path the worm moves along

  public SandwormMoveDemandMessage(final List<Position> path) {
    super(MessageType.SANDWORM_MOVE_DEMAND);
    this.path = path;
  }
}

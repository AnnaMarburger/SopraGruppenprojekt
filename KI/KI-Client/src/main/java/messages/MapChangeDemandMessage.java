package messages;

import messages.data.ChangeReason;
import messages.data.MessageType;
import messages.data.tile.MessageTile;
import shared_data.Position;

/**
 * This class represents a map change demand message.
 */
public class MapChangeDemandMessage extends Message {

  public final ChangeReason changeReason; // reason of change
  public final MessageTile[][] newMap; // new map
  public final Position stormEye; // position of eye of storm

  public MapChangeDemandMessage(final ChangeReason changeReason, final MessageTile[][] newMap,
      final Position stormEye) {
    super(MessageType.MAP_CHANGE_DEMAND);
    this.changeReason = changeReason;
    this.newMap = newMap;
    this.stormEye = stormEye;
  }
}

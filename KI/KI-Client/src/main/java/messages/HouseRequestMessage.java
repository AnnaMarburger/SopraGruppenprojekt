package messages;

import messages.data.MessageType;
import shared_data.house.HouseName;

/**
 * This class represents a house request message.
 */
public class HouseRequestMessage extends Message {

  public final HouseName houseName; // requested house name

  public HouseRequestMessage(final HouseName houseName) {
    super(MessageType.HOUSE_REQUEST);
    this.houseName = houseName;
  }
}
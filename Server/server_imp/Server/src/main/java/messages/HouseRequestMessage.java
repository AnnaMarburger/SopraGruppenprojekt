package messages;

import shared_data.house.HouseName;
import messages.data.MessageType;

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
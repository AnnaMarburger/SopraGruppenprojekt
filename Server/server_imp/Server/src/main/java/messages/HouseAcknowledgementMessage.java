package messages;

import shared_data.house.HouseName;
import messages.data.MessageType;

/**
 * This class represents a house acknowledgement message.
 */
public class HouseAcknowledgementMessage extends Message {

  public final int clientID; // client id
  public final HouseName houseName; // acknowledged house name

  public HouseAcknowledgementMessage(final int clientID, final HouseName houseName) {
    super(MessageType.HOUSE_ACKNOWLEDGEMENT);
    this.clientID = clientID;
    this.houseName = houseName;
  }
}

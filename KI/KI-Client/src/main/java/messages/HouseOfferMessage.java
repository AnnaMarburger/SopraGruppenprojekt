package messages;

import messages.data.MessageType;
import shared_data.house.House;

import java.util.List;

/**
 * This class represents a house offer message.
 */
public class HouseOfferMessage extends Message {

  public final int clientID; // client id
  public final List<House> houses; // list of houses the player can select from

  public HouseOfferMessage(final int clientID, final List<House> houses) {
    super(MessageType.HOUSE_OFFER);
    this.clientID = clientID;
    this.houses = houses;
  }
}

package shared_data.house;

import java.util.List;
import messages.data.character.MessageCharacter;

/**
 * This class represents a house in the message format.
 */
public class House {

  public final HouseName houseName;
  public final HouseColor houseColor;
  public final List<MessageCharacter> houseMessageCharacters;

  public House(final HouseName houseName, final HouseColor houseColor,
      final List<MessageCharacter> houseMessageCharacters) {
    this.houseName = houseName;
    this.houseColor = houseColor;
    this.houseMessageCharacters = houseMessageCharacters;
  }

  @Override
  public String toString() {
    return "{\n"
        + "houseName: " + houseName + ",\n"
        + "houseColor: " + houseColor + ",\n"
        + "houseCharacters: " + houseMessageCharacters + "\n"
        + "}";
  }
}

package shared_data.house;

/**
 * This class represents the concrete house Atreides.
 */
public class HouseAtreides extends House {

  public HouseAtreides() {
    super(HouseName.ATREIDES, HouseColor.GREEN,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.ATREIDES));
  }
}

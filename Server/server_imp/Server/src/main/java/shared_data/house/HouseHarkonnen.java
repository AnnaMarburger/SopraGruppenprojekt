package shared_data.house;

/**
 * This class represents the concrete house Harkonnen.
 */
public class HouseHarkonnen extends House {

  public HouseHarkonnen() {
    super(HouseName.HARKONNEN, HouseColor.RED,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.HARKONNEN));
  }
}

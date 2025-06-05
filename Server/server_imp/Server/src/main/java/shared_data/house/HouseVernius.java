package shared_data.house;

/**
 * This class represents the concrete house Vernius.
 */
public class HouseVernius extends House {

  public HouseVernius() {
    super(HouseName.VERNIUS, HouseColor.VIOLET,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.VERNIUS));
  }
}

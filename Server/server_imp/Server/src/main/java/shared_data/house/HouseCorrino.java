package shared_data.house;

/**
 * This class represents the concrete house Corrino.
 */
public class HouseCorrino extends House {

  public HouseCorrino() {
    super(HouseName.CORRINO, HouseColor.GOLD,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.CORRINO));
  }
}

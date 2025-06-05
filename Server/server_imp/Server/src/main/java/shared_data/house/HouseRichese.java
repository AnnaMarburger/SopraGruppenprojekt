package shared_data.house;

/**
 * This class represents the concrete house Richese.
 */
public class HouseRichese extends House {

  public HouseRichese() {
    super(HouseName.RICHESE, HouseColor.SILVER,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.RICHESE));
  }
}

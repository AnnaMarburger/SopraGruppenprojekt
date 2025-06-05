package shared_data.house;

/**
 * This class represents the concrete house Ordos.
 */
public class HouseOrdos extends House {

  public HouseOrdos() {
    super(HouseName.ORDOS, HouseColor.BLUE,
        HouseCharacterMap.getInstance().getCharactersForHouse(HouseName.ORDOS));
  }
}

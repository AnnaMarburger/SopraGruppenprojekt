package shared_data.house;

import static shared_data.CONST.random;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum represents all different names of houses.
 */
public enum HouseName {
  CORRINO,
  ATREIDES,
  HARKONNEN,
  ORDOS,
  RICHESE,
  VERNIUS;

  private static final List<HouseName> VALUES = List.of(values());
  private static final int SIZE = VALUES.size();

  /**
   * This method returns one random house name.
   *
   * @return random house name
   */
  public static HouseName randomHouseName() {
    return VALUES.get(random.nextInt(SIZE));
  }

  /**
   * This method returns all 4 houses that are left except the two given house names.
   *
   * @param houseName1 house name of one player
   * @param houseName2 house name of the other player
   * @return list of house names that are not already in the game
   */
  public static List<HouseName> getAtomicHelpHouseNames(HouseName houseName1,
      HouseName houseName2) {
    List<HouseName> tmp = new ArrayList<>();
    for (HouseName houseName : HouseName.values()) {
      if (houseName != houseName1 && houseName != houseName2) {
        tmp.add(houseName);
      }
    }
    return tmp;
  }
}

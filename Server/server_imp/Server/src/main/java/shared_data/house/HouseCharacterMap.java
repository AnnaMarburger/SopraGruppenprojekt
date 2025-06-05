package shared_data.house;

import static shared_data.CONST.random;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import messages.data.character.MessageCharacter;
import shared_data.character.CharacterType;


/**
 * This class maps all different characters to their corresponding house.
 */
public class HouseCharacterMap {

  private final Map<HouseName, List<MessageCharacter>> map; // map of a house name to a list of
  // all its characters in the message format

  private static final HouseCharacterMap houseCharacterMap = new HouseCharacterMap();

  private HouseCharacterMap() {
    map = new EnumMap<>(HouseName.class);
    inputData();
  }

  public static HouseCharacterMap getInstance() {
    return houseCharacterMap;
  }

  private void inputData() {
    //House Corrino (Farbe Gold)
    List<MessageCharacter> corrinoMessageCharacters = new ArrayList<>();
    corrinoMessageCharacters.add(new MessageCharacter("Emperor Shaddam IV Corrino", CharacterType.NOBLE));
    corrinoMessageCharacters.add(new MessageCharacter("Princess Irulan Corrino", CharacterType.BENE_GESSERIT));
    corrinoMessageCharacters.add(new MessageCharacter("Count Hasimir Fenring", CharacterType.MENTAT));
    corrinoMessageCharacters.add(new MessageCharacter("Lady Margot Fenring", CharacterType.BENE_GESSERIT));
    corrinoMessageCharacters.add(new MessageCharacter("Reverend Mother Gaius Helen Mohiam", CharacterType.BENE_GESSERIT));
    corrinoMessageCharacters.add(new MessageCharacter("Captain Aramsham", CharacterType.FIGHTER));
    map.put(HouseName.CORRINO, corrinoMessageCharacters);

    //House Atreides (Farbe Grün)
    List<MessageCharacter> atreidesMessageCharacters = new ArrayList<>();
    atreidesMessageCharacters.add(new MessageCharacter("Duke Leto Atreides", CharacterType.NOBLE));
    atreidesMessageCharacters.add(new MessageCharacter("Paul Atreides", CharacterType.NOBLE));
    atreidesMessageCharacters.add(new MessageCharacter("Lady Jessica", CharacterType.BENE_GESSERIT));
    atreidesMessageCharacters.add(new MessageCharacter("Thufir Hawat", CharacterType.MENTAT));
    atreidesMessageCharacters.add(new MessageCharacter("Gurney Halleck", CharacterType.FIGHTER));
    atreidesMessageCharacters.add(new MessageCharacter("Space Pug, Duke Letos tapferer Mopshund", CharacterType.FIGHTER));
    map.put(HouseName.ATREIDES, atreidesMessageCharacters);

    //House Harkonnen (Farbe Rot)
    List<MessageCharacter> harkonnenMessageCharacters = new ArrayList<>();
    harkonnenMessageCharacters.add(new MessageCharacter("Baron Vladimir Harkonnen", CharacterType.NOBLE));
    harkonnenMessageCharacters.add(new MessageCharacter("Count Glossu Beast Rabban", CharacterType.FIGHTER));
    harkonnenMessageCharacters.add(new MessageCharacter("Feyd-Rautha Rabban", CharacterType.FIGHTER));
    harkonnenMessageCharacters.add(new MessageCharacter("Piter De Vries", CharacterType.MENTAT));
    harkonnenMessageCharacters.add(new MessageCharacter("Iakin Nefud", CharacterType.FIGHTER));
    harkonnenMessageCharacters.add(new MessageCharacter("Pet Spider", CharacterType.FIGHTER));
    map.put(HouseName.HARKONNEN, harkonnenMessageCharacters);

    //House Ordos (Farbe Blau)
    List<MessageCharacter> ordosMessageCharacters = new ArrayList<>();
    ordosMessageCharacters.add(new MessageCharacter("Executrix", CharacterType.NOBLE));
    ordosMessageCharacters.add(new MessageCharacter("The Speaker", CharacterType.NOBLE));
    ordosMessageCharacters.add(new MessageCharacter("Ammon", CharacterType.MENTAT));
    ordosMessageCharacters.add(new MessageCharacter("Edric", CharacterType.MENTAT));
    ordosMessageCharacters.add(new MessageCharacter("Roma Atani", CharacterType.MENTAT));
    ordosMessageCharacters.add(new MessageCharacter("Robot", CharacterType.FIGHTER));
    map.put(HouseName.ORDOS, ordosMessageCharacters);

    //House Richese (Farbe Silber)
    List<MessageCharacter> richeseMessageCharacters = new ArrayList<>();
    richeseMessageCharacters.add(new MessageCharacter("Count Ilban Richese", CharacterType.NOBLE));
    richeseMessageCharacters.add(new MessageCharacter("Helena Richese", CharacterType.NOBLE));
    richeseMessageCharacters.add(new MessageCharacter("Haloa Rund", CharacterType.MENTAT));
    richeseMessageCharacters.add(new MessageCharacter("Flinto Kinnis", CharacterType.MENTAT));
    richeseMessageCharacters.add(new MessageCharacter("Tenu Chobyn", CharacterType.MENTAT));
    richeseMessageCharacters.add(new MessageCharacter("Yresk", CharacterType.FIGHTER));
    map.put(HouseName.RICHESE, richeseMessageCharacters);

    //House Vernius (Farbe Violett)
    List<MessageCharacter> verniusMessageCharacters = new ArrayList<>();
    verniusMessageCharacters.add(new MessageCharacter("Earl Dominic Vernius", CharacterType.NOBLE));
    verniusMessageCharacters.add(new MessageCharacter("Lady Shando Vernius", CharacterType.NOBLE));
    verniusMessageCharacters.add(new MessageCharacter("Kailea Vernius", CharacterType.NOBLE));
    verniusMessageCharacters.add(new MessageCharacter("Tessia Vernius", CharacterType.BENE_GESSERIT));
    verniusMessageCharacters.add(new MessageCharacter("Rhombur Vernius", CharacterType.FIGHTER));
    verniusMessageCharacters.add(new MessageCharacter("Bronso Vernius", CharacterType.MENTAT));
    map.put(HouseName.VERNIUS, verniusMessageCharacters);
  }

  /**
   * This method returns all characters in the message format for the given house name.
   *
   * @param houseName house name the characters shall be from
   * @return list of characters in the message format
   */
  public List<MessageCharacter> getCharactersForHouse(HouseName houseName) {
    return map.get(houseName);
  }

  /**
   * This method returns one character on the message format from the given house name.
   *
   * @param houseName house name the character shall be from
   * @return one character in the message format
   */
  public MessageCharacter getCharacterForHouse(HouseName houseName) {
    return map.get(houseName).get(random.nextInt(6));
  }
}

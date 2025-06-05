package messages.data.dictionary;

/**
 * This class represents stats for characters used in the message format.
 */
public class Stats {

  public final int HP;
  public final int AP;
  public final int MP;
  public final int spice;
  public final boolean isLoud;
  public final boolean isSwallowed;

  public Stats(final int HP, final int AP, final int MP, final int spice, final boolean isLoud,
      final boolean isSwallowed) {
    this.HP = HP;
    this.AP = AP;
    this.MP = MP;
    this.spice = spice;
    this.isLoud = isLoud;
    this.isSwallowed = isSwallowed;
  }

  @Override
  public String toString() {
    return "{\n"
        + "HP: " + HP + ",\n"
        + "AP: " + AP + ",\n"
        + "MP: " + MP + ",\n"
        + "spice: " + spice + ",\n"
        + "isLoud: " + isLoud + ",\n"
        + "isSwallowed: " + isSwallowed + "\n"
        + "}";
  }
}

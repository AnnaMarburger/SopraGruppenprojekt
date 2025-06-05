package messages.data.dictionary;

import shared_data.character.CharacterType;

/**
 * This class represents attributes that are used for characters in the message format.
 */
public class Attributes {

  public final CharacterType characterType;
  public final int healthMax;
  public final int healthCurrent;
  public final int healingHP;
  public final int mpMax;
  public final int mpCurrent;
  public final int apMax;
  public final int apCurrent;
  public final int attackDamage;
  public final int inventorySize;
  public final int inventoryUsed;
  public final boolean killedBySandworm;
  public final boolean isLoud;

  public Attributes(final CharacterType characterType, final int healthMax, final int healthCurrent,
      final int healingHP, final int mpMax, final int mpCurrent, final int apMax,
      final int apCurrent, final int attackDamage, final int inventorySize, final int inventoryUsed,
      final boolean killedBySandworm, final boolean isLoud) {
    this.characterType = characterType;
    this.healthMax = healthMax;
    this.healthCurrent = healthCurrent;
    this.healingHP = healingHP;
    this.mpMax = mpMax;
    this.mpCurrent = mpCurrent;
    this.apMax = apMax;
    this.apCurrent = apCurrent;
    this.attackDamage = attackDamage;
    this.inventorySize = inventorySize;
    this.inventoryUsed = inventoryUsed;
    this.killedBySandworm = killedBySandworm;
    this.isLoud = isLoud;
  }

  @Override
  public String toString() {
    return "{\n"
        + "characterType: " + characterType + ",\n"
        + "healthMax: " + healthMax + ",\n"
        + "healthCurrent: " + healthCurrent + ",\n"
        + "healingHP: " + healingHP + ",\n"
        + "mpMax: " + mpMax + ",\n"
        + "mpCurrent: " + mpCurrent + ",\n"
        + "apMax: " + apMax + ",\n"
        + "apCurrent: " + apCurrent + ",\n"
        + "attackDamage: " + attackDamage + ",\n"
        + "inventorySize: " + inventorySize + ",\n"
        + "inventoryUsed: " + inventoryUsed + ",\n"
        + "killedBySandworm: " + killedBySandworm + ",\n"
        + "isLoud: " + isLoud + "\n"
        + "}";
  }
}
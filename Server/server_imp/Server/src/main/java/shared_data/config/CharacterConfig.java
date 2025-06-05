package shared_data.config;

/**
 * This class represents an abstract character config.
 * Used to save default values when a new character is spawned.
 */
public abstract class CharacterConfig {

  public final int maxHP;
  public final int maxMP;
  public final int maxAP;
  public final int damage;
  public final int inventorySize;
  public final int healingHP;

  protected CharacterConfig(final int maxHP, final int maxMP, final int maxAP, final int damage,
      final int inventorySize, final int healingHP) {
    this.maxHP = maxHP;
    this.maxMP = maxMP;
    this.maxAP = maxAP;
    this.damage = damage;
    this.inventorySize = inventorySize;
    this.healingHP = healingHP;
  }

  @Override
  public String toString() {
    return "{\n"
        + "maxHP: " + maxHP + ",\n"
        + "maxMP: " + maxMP + ",\n"
        + "maxAP: " + maxAP + ",\n"
        + "damage: " + damage + ",\n"
        + "inventorySize: " + inventorySize + ",\n"
        + "healingHP: " + healingHP + "\n"
        + "}";
  }
}

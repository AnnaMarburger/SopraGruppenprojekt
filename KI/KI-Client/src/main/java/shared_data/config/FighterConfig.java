package shared_data.config;

/**
 * Concrete character config of type fighter.
 */
public class FighterConfig extends CharacterConfig {

  protected FighterConfig(final int maxHP, final int maxMP, final int maxAP, final int damage,
      final int inventorySize, final int healingHP) {
    super(maxHP, maxMP, maxAP, damage, inventorySize, healingHP);
  }
}

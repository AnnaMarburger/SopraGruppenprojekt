package shared_data.config;

/**
 * Concrete character config of type mentat.
 */
public class MentatConfig extends CharacterConfig {

  protected MentatConfig(final int maxHP, final int maxMP, final int maxAP, final int damage,
      final int inventorySize, final int healingHP) {
    super(maxHP, maxMP, maxAP, damage, inventorySize, healingHP);
  }
}

package shared_data.config;

import com.google.gson.Gson;
import messages.data.tile.MessageTileType;

import java.util.Arrays;
import shared_data.CONST;

/**
 * This class represents the scenario config in the message format.
 */
public class ScenarioConfig {

  public final MessageTileType[][] scenario;

  public static ScenarioConfig getCurrentConfig() {
    if (currentConfig == null) {
      currentConfig = loadDefault();
      CONST.logger.severe("COULDN'T LOAD SCENARIO CONFIG. USED DEFAULT.");
    }
    return currentConfig;
  }

  public static void setCurrentConfig(ScenarioConfig currentConfig) {
    ScenarioConfig.currentConfig = currentConfig;
  }

  private static ScenarioConfig currentConfig;

  private ScenarioConfig(final MessageTileType[][] scenario) {
    this.scenario = scenario;
  }

  private static ScenarioConfig loadDefault() {
    var g = new Gson();
    return g.fromJson(CONST.DEFAULT_SCENARIO_CONFIG, ScenarioConfig.class);
  }

  @Override
  public String toString() {
    return "{\n"
        + "scenario: " + Arrays.deepToString(scenario) + "\n"
        + "}";
  }
}

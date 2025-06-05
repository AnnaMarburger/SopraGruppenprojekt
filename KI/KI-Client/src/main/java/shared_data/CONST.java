package shared_data;

import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * This class saves various data for constant use.
 */
public class CONST {

  public static final SecureRandom random = new SecureRandom();

  public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  public static final String VERSION = "1.0";
  public static final String PARTY_CONFIG_SCHEMA_PATH = "schemas/party_config_schema.json";
  public static final String SCENARIO_CONFIG_SCHEMA_PATH = "schemas/scenario_config_schema.json";

  //region Exception Strings
  public static final String CHARACTER_ID_EXCEPTION = "Could not find character controller for "
      + "character id!";
  public static final String NOT_ENOUGH_AP_NORMAL_EXCEPTION = "Character has not enough ap to "
      + "execute a normal action!";
  public static final String NOT_ENOUGH_AP_SPECIAL_EXCEPTION = "Character has not enough ap to "
      + "execute a special action!";
  public static final String CHARACTER_POSITION_EXCEPTION = "Could not find character "
      + "controller for position!";
  public static final String NOT_CORRECT_TYPE_EXCEPTION =
      "Character has not correct character type to "
          + "execute this special action!";
  public static final String ILLEGAL_MESSAGE = "Illegal message!";
  public static final String NOT_ALLOWED_TO_DO_ANYTHING_EXCEPTION = "Character is not allowed to "
      + "do anything!";
  public static final String SPECTATOR_NOT_ALLOWED_EXCEPTION = "Spectators are not allowed to "
      + "send messages!";

  public static final String NOT_ENOUGH_ATOM_BOMBS = "Player has not enough atom bombs!";
  public static final String CHARACTER_CONFIG_IS_NULL = "Character config is null!";
  public static final String SHOULD_NOT_REACH_HERE = "Should not reach here!";
  public static final String CHARACTER_IN_SANDSTORM= "Cannot execute Action, target character is "
      + "in sandstorm!";
  //endregion

  public static final String DEFAULT_SCENARIO_CONFIG = """
      {
        "scenario": [
          ["CITY" ,"MOUNTAINS" ,"PLATEAU" ,"PLATEAU" ,"FLAT_SAND"],
          ["FLAT_SAND" ,"FLAT_SAND" ,"FLAT_SAND" ,"FLAT_SAND" ,"FLAT_SAND"],
          ["PLATEAU" ,"FLAT_SAND" ,"PLATEAU" ,"DUNE" ,"FLAT_SAND"],
          ["DUNE" ,"FLAT_SAND" ,"MOUNTAINS" ,"FLAT_SAND" ,"FLAT_SAND"],
          ["FLAT_SAND" ,"DUNE" ,"FLAT_SAND" ,"FLAT_SAND" ,"CITY" ]
        ]
      }""";

  public static final String DEFAULT_PARTY_CONFIG = """
      {
      "noble": {
          "maxHP": 20,
          "maxMP": 21,
          "maxAP": 22,
          "damage": 4,
          "inventorySize": 8,
          "healingHP": 6
        },
        "mentat": {
          "maxHP": 20,
          "maxMP": 21,
          "maxAP": 22,
          "damage": 4,
          "inventorySize": 8,
          "healingHP": 6
        },
        "beneGesserit": {
          "maxHP": 20,
          "maxMP": 21,
          "maxAP": 22,
          "damage": 4,
          "inventorySize": 8,
          "healingHP": 6
        },
        "fighter": {
          "maxHP": 20,
          "maxMP": 21,
          "maxAP": 22,
          "damage": 4,
          "inventorySize": 8,
          "healingHP": 6
        },
        "numbOfRounds": 10,
        "actionTimeUserClient": 60000,
        "actionTimeAiClient": 60000,
        "highGroundBonusRatio": 0.5,
        "lowerGroundMalusRatio": 0.5,
        "kanlySuccessProbability": 0.5,
        "spiceMinimum": 10,
        "cellularAutomaton": "B2/S3",
        "sandWormSpeed": 10,
        "sandWormSpawnDistance": 10,
        "cloneProbability": 0.5,
        "minPauseTime": 10,
        "maxStrikes" : 10
      }""";

  private CONST() {
  }
}

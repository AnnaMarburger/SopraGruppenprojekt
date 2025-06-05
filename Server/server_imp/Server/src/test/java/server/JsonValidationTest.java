package server;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

class JsonValidationTest {

  @Test
  void testPartyConfigValidation() {
    String partyConfigJson = """
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
          "actionTimeUserClient": 5000,
          "actionTimeAiClient": 1000,
          "highGroundBonusRatio": 0.5,
          "lowerGroundMalusRatio": 0.5,
          "kanlySuccessProbability": 0.5,
          "crashProbability": 0.5,
          "spiceMinimum": 10,
          "cellularAutomaton": "B2/S3",
          "sandWormSpeed": 10,
          "sandWormSpawnDistance": 10,
          "cloneProbability": 0.5,
          "minPauseTime": 10,
          "maxStrikes" : 10
        }""";

    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream is = classLoader.getResourceAsStream("schemas/party_config_schema.json");
      if (is == null) {
        fail();
      }
      JSONObject rawSchema = new JSONObject(new JSONTokener(is));
      Schema schema = SchemaLoader.load(rawSchema);
      schema.validate(new JSONObject(partyConfigJson));
    } catch (ValidationException e) {
      System.out.println(e.getMessage());
      fail();
    }
  }
}

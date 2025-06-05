package shared_data.config;

import com.google.gson.Gson;
import shared_data.CONST;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class represents the party config in the message format.
 */
public class PartyConfig {

  private static PartyConfig currentConfig;

  public final NobleConfig noble;
  public final MentatConfig mentat;
  public final BeneGesseritConfig beneGesserit;
  public final FighterConfig fighter;
  public final int numbOfRounds;
  public final int actionTimeUserClient;
  public final int actionTimeAiClient;
  public final float highGroundBonusRatio;
  public final float lowGroundMalusRatio;
  public final float kanlySuccessProbability;
  public final int spiceMinimum;
  public final String cellularAutomaton;

  public final float crashProbability;
  public final int sandWormSpeed;
  public final int sandWormSpawnDistance;
  public final float cloneProbability;
  public final int minPauseTime;
  public final int maxStrikes;

  public List<Integer> caBorn; // list of born-values for the automaton
  public List<Integer> caSurvive; // list of survive-values for the automaton

  private PartyConfig(final NobleConfig noble, final MentatConfig mentat,
      final BeneGesseritConfig beneGesserit, final FighterConfig fighter, final int numbOfRounds,
      final int actionTimeUserClient, final int actionTimeAiClient,
      final float highGroundBonusRatio, final float lowGroundMalusRatio,
      final float kanlySuccessProbability, final int spiceMinimum, final String cellularAutomaton, final float crashProbability,
      final int sandWormSpeed, final int sandWormSpawnDistance, final float cloneProbability,
      final int minPauseTime, final int maxStrikes) {
    this.noble = noble;
    this.mentat = mentat;
    this.beneGesserit = beneGesserit;
    this.fighter = fighter;
    this.numbOfRounds = numbOfRounds;
    this.actionTimeUserClient = actionTimeUserClient;
    this.actionTimeAiClient = actionTimeAiClient;
    this.highGroundBonusRatio = highGroundBonusRatio;
    this.lowGroundMalusRatio = lowGroundMalusRatio;
    this.kanlySuccessProbability = kanlySuccessProbability;
    this.spiceMinimum = spiceMinimum;
    this.cellularAutomaton = cellularAutomaton;
    this.crashProbability = crashProbability;
    this.sandWormSpeed = sandWormSpeed;
    this.sandWormSpawnDistance = sandWormSpawnDistance;
    this.cloneProbability = cloneProbability;
    this.minPauseTime = minPauseTime;
    this.maxStrikes = maxStrikes;

    filterAutomatonSettings();
  }

  public static PartyConfig getCurrentConfig() {
    if (currentConfig == null) {
      currentConfig = loadDefault();
      CONST.logger.severe("COULDN'T LOAD PARTY CONFIG. USED DEFAULT.");
    }
    return currentConfig;
  }

  public static void setCurrentConfig(PartyConfig currentConfig) {
    PartyConfig.currentConfig = currentConfig;
    PartyConfig.getCurrentConfig().init();
  }

  /**
   * This method filters the automaton settings and puts the values in the lists caBorn and
   * caSurvive.
   */
  private void filterAutomatonSettings() {
    var pattern = Pattern.compile("B(?<b>[0-8]*)/S(?<s>[0-8]*)");
    var matcher = pattern.matcher(cellularAutomaton);

    if (matcher.find()) {
      String b = matcher.group("b");
      for (var i = 0; i < b.length(); i++) {
        caBorn.add(Character.getNumericValue(b.charAt(i)));
      }
      String s = matcher.group("s");
      for (var i = 0; i < s.length(); i++) {
        caSurvive.add(Character.getNumericValue(s.charAt(i)));
      }
    }
  }

  public void init() {
    caBorn = new ArrayList<>();
    caSurvive = new ArrayList<>();
    filterAutomatonSettings();
  }

  @Override
  public String toString() {
    return "{\n"
        + "noble: " + noble + ",\n"
        + "mentat: " + mentat + ",\n"
        + "beneGesserit: " + beneGesserit + ",\n"
        + "fighter: " + fighter + ",\n"
        + "numbOfRounds: " + numbOfRounds + ",\n"
        + "actionTimeUserClient: " + actionTimeUserClient + ",\n"
        + "actionTimeAiClient: " + actionTimeAiClient + ",\n"
        + "highGroundBonusRatio: " + highGroundBonusRatio + ",\n"
        + "lowGroundMalusRatio: " + lowGroundMalusRatio + ",\n"
        + "kanlySuccessProbability: " + kanlySuccessProbability + ",\n"
        + "spiceMinimum: " + spiceMinimum + ",\n"
        + "cellularAutomaton:" + cellularAutomaton + ",\n"
        + "sandWormSpeed: " + sandWormSpeed + ",\n"
        + "sandWormSpawnDistance: " + sandWormSpawnDistance + ",\n"
        + "cloneProbability: " + cloneProbability + ",\n"
        + "minPauseTime: " + minPauseTime + ",\n"
        + "maxStrikes: " + maxStrikes + "\n"
        + "}";
  }


  private static PartyConfig loadDefault() {
    var g = new Gson();
    return g.fromJson(CONST.DEFAULT_PARTY_CONFIG, PartyConfig.class);
  }
}

package messages;

import messages.data.MessageType;
import shared_data.CityToClient;
import shared_data.Position;
import shared_data.config.PartyConfig;
import shared_data.config.ScenarioConfig;

/**
 * This class represents a game config message.
 */
public class GamecfgMessage extends Message {

  public final ScenarioConfig scenario; // scenario the current game is using
  public final PartyConfig party; // party config the current game is using
  public final CityToClient[] citiesToClients; // specification which player has which city
  public final Position stormEye; // position if the eye of the storm

  public GamecfgMessage(final ScenarioConfig scenario, final PartyConfig party,
      final CityToClient[] citiesToClients, final Position stormEye) {
    super(MessageType.GAMECFG);
    this.scenario = scenario;
    this.party = party;
    this.citiesToClients = citiesToClients;
    this.stormEye = stormEye;
  }
}

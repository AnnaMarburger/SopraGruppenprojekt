package server;


import static shared_data.CONST.logger;

import com.google.gson.Gson;

import exceptions.ServerErrorException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import messages.*;
import messages.data.*;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import shared_data.CONST;
import shared_data.config.PartyConfig;
import shared_data.config.ScenarioConfig;

/**
 * This class parses all different kinds of messages.
 */
public class MessageParser {

  static Gson gson = new Gson();

  private MessageParser() {
  }

  /**
   * This method parses a json string to a concrete message.
   *
   * @param json json string of a message
   * @return Optional with a concrete message
   */
  public static Optional<Message> parseMessage(String json) {
    MessageType messageType;
    try {
      messageType = findMessageType(json);
    } catch (Exception e) {
      messageType = MessageType.FAIL;
    }

    Message message;
    switch (messageType) {
      case JOIN -> message = gson.fromJson(json, JoinMessage.class);
      case REJOIN -> message = gson.fromJson(json, ReJoinMessage.class);
      case GAMECFG -> message = gson.fromJson(json, GamecfgMessage.class);
      case HOUSE_REQUEST -> message = gson.fromJson(json, HouseRequestMessage.class);
      case MOVEMENT_REQUEST -> message = gson.fromJson(json, MovementRequestMessage.class);
      case ACTION_REQUEST -> message = gson.fromJson(json, ActionRequestMessage.class);
      case TRANSFER_REQUEST -> message = gson.fromJson(json, TransferRequestMessage.class);
      case END_TURN_REQUEST -> message = gson.fromJson(json, EndTurnRequestMessage.class);
      case GAMESTATE_REQUEST -> message = gson.fromJson(json, GamestateRequestMessage.class);
      case PAUSE_REQUEST -> message = gson.fromJson(json, PauseRequestMessage.class);
      case HELI_REQUEST -> message = gson.fromJson(json, HeliRequestMessage.class); //Heliport
      case DEBUG -> message = gson.fromJson(json, DebugMessage.class);
      default -> message = null;
    }

    return Optional.ofNullable(message);
  }

  /**
   * This method finds the type of message in the json format.
   *
   * @param json json string
   * @return MessageType of the json string
   */
  public static MessageType findMessageType(String json) {
    var typeHolder = gson.fromJson(json, TypeHolder.class);
    return MessageType.valueOf(typeHolder.type);
  }

  /**
   * This method finds the client id of the message in the json format.
   *
   * @param message json string
   * @return Optional with the client id
   */
  public static Optional<Integer> getClientIDFromMessage(Message message) {
    return Optional.of(gson.fromJson(message.toString(), ClientHolder.class).clientID);
  }

  /**
   * This method validated a given json with a given schema.
   *
   * @param schemaPath path of the internal schema file
   * @param json json string
   * @return true if valid, else false
   */
  private static boolean validateJson(String schemaPath, String json) {
    try {
      var classLoader = Thread.currentThread().getContextClassLoader();
      InputStream is = classLoader.getResourceAsStream(schemaPath);
      JSONObject rawSchema = null;
      if (is != null) {
        rawSchema = new JSONObject(new JSONTokener(is));
      }
      var schema = SchemaLoader.load(rawSchema);
      schema.validate(new JSONObject(json));
    } catch (ValidationException e) {
      logger.severe(e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * This inner class is used to find the type of message.
   */
  public static class TypeHolder {

    public final String type;

    public TypeHolder(final String type) {
      this.type = type;
    }
  }

  /**
   * This inner class is used to find the client id in a message.
   */
  public static class ClientHolder {

    public final int clientID;

    public ClientHolder(final int clientID) {
      this.clientID = clientID;
    }
  }

  /**
   * This method parses the scenario config.
   *
   * @param json json string
   * @return ScenarioConfig
   */
  public static ScenarioConfig parseScenarioConfig(String json) {
    if (validateJson(CONST.SCENARIO_CONFIG_SCHEMA_PATH, json)) {
      return gson.fromJson(json, ScenarioConfig.class);
    }
    return null;
  }

  /**
   * This method parses the party config.
   *
   * @param json json string
   * @return PartyConfig
   */
  public static PartyConfig parsePartyConfig(String json) {
    if (validateJson(CONST.PARTY_CONFIG_SCHEMA_PATH, json)) {
      return gson.fromJson(json, PartyConfig.class);
    }
    return null;
  }

  /**
   * This method reads an external file and returns its contents as a string.
   *
   * @param path external path
   * @return string of contents
   */
  public static String getExternalFileContent(String path) {
    try {
      return Files.readString(Path.of(path));
    } catch (IOException e) {
      logger.info("Config path not correct!");
      throw new ServerErrorException(e.getMessage());
    }
  }
}

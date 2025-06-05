package server;

import static shared_data.CONST.logger;

import exceptions.ServerErrorException;
import java.net.InetSocketAddress;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import server.gameobjects.Game;
import shared_data.Tupel;
import shared_data.config.PartyConfig;
import shared_data.config.ScenarioConfig;

/**
 * Main server class.
 */
public class ServerMain extends WebSocketServer {

  //Static Variables
  private static ServerMain s;

  private final LinkedBlockingDeque<Tupel<WebSocket, String>> ingoingMessageQueue = new LinkedBlockingDeque<>();
  private boolean processMessages = true;

  private static String partyConfigPath = "config.party.json";
  private static String scenarioConfigPath = "config.scenario.json";

  public static void main(String[] args) {
    if (!checkArguments(args) && !checkEnvs(System.getenv())) {
      logger.severe("Couldn't load config paths - using default!");
    }

    init();
  }

  private static boolean checkArguments(String[] args) {
    logger.log(Level.INFO, "args: {0}", Arrays.toString(args));
    var temp1 = false;
    var temp2 = false;
    try {
      for (var i = 0; i < args.length; i++) {
        if (args[i].equals("--")) {
          if (args[i + 1].equals("party-config")) {
            partyConfigPath = args[i + 2];
            temp1 = true;
            continue;
          }
          if (args[i + 1].equals("scenario-config")) {
            scenarioConfigPath = args[i + 2];
            temp2 = true;
            continue;
          }
        }
        if (args[i].equals("-m")) {
          partyConfigPath = args[i + 1];
          temp1 = true;
          continue;
        }
        if (args[i].equals("-s")) {
          scenarioConfigPath = args[i + 1];
          temp2 = true;
        }
      }
    } catch (Exception e) {
      logger.severe("No program variables found!");
      return false;
    }
    return temp1 && temp2;
  }

  private static boolean checkEnvs(Map<String, String> getenv) {
    logger.log(Level.INFO, "envs: {0}", getenv);
    var temp1 = false;
    var temp2 = false;
    try {
      for (Entry<String, String> entry : getenv.entrySet()) {
        if ("CONFIG_PARTY".equals(entry.getKey())) {
          partyConfigPath = entry.getValue();
          temp1 = true;
        } else if ("CONFIG_SCENARIO".equals(entry.getKey())) {
          scenarioConfigPath = entry.getValue();
          temp2 = true;
        }
      }
    } catch (Exception e) {
      logger.severe("Couldn't load environment variables!");
      return false;
    }
    return temp1 && temp2;
  }

  public ServerMain(int port) {
    super(new InetSocketAddress(port));
    new Thread(this::ingoingMessageQueueThread).start();
  }

  public static void closeServer() {
    try {
      s.stop();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ServerErrorException("Closing Server!");
    }
  }

  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    logger.log(Level.INFO, "Websocket: {0}", webSocket);
  }

  @Override
  public void onClose(WebSocket webSocket, int i, String s, boolean b) {
  }

  @Override
  public void onMessage(WebSocket webSocket, String s) {
    logger.log(Level.INFO, "Message: {0}", s);
    ingoingMessageQueue.add(new Tupel<>(webSocket, s));
  }

  @Override
  public void onError(WebSocket webSocket, Exception e) {
    logger.log(Level.INFO, "Message: {0}", s);
    try {
      s.stop();
      processMessages = false;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new ServerErrorException(ex.getMessage());
    }
  }

  @Override
  public void onStart() {
    logger.info("Start");
    MessageHandler.getInstance().setServer(this);
  }

  private static void createWebSocketServer() {
    try {
      s = new ServerMain(10191);
      s.start();
    } catch (Exception e) {
      closeServer();
    }
  }

  public static void init() {
    createWebSocketServer();
    loadScenario();
    loadParty();
    Game.createGame();
  }

  private static void loadScenario() {
    ScenarioConfig.setCurrentConfig(MessageParser.parseScenarioConfig(
        MessageParser.getExternalFileContent(scenarioConfigPath)));
    logger.info("Successfully loaded scenario config!");
  }

  private static void loadParty() {
    PartyConfig.setCurrentConfig(
        MessageParser.parsePartyConfig(MessageParser.getExternalFileContent(partyConfigPath)));
    logger.info("Successfully loaded party config!");
  }

  private void ingoingMessageQueueThread() {
    try {
      while (processMessages) {
        Tupel<WebSocket, String> message = ingoingMessageQueue.take();
        MessageHandler.getInstance().processMessage(message.second, message.first);
      }
    } catch (InterruptedException e) {
      logger.severe("Message thread is dead.");
      Thread.currentThread().interrupt();
    }
  }
}
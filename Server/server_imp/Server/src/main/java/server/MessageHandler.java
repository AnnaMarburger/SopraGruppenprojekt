package server;

import static shared_data.CONST.ILLEGAL_MESSAGE;
import static shared_data.CONST.NOT_ALLOWED_TO_DO_ANYTHING_EXCEPTION;
import static shared_data.CONST.SPECTATOR_NOT_ALLOWED_EXCEPTION;
import static shared_data.CONST.logger;

import exceptions.ServerErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.stream.Collectors;
import messages.*;
import messages.Message;
import messages.data.MessageType;
import org.java_websocket.WebSocket;
import server.gameobjects.Game;
import server.gameobjects.GameState;
import server.gameobjects.GameTimer;
import server.gameobjects.Player;
import shared_data.CONST;
import shared_data.CityToClient;
import shared_data.config.PartyConfig;
import shared_data.config.ScenarioConfig;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class handles all incoming messages and distributes them accordingly.
 */
public class MessageHandler {

  //region Private vars
  private ServerMain serverMain;

  private static GamecfgMessage gamecfgMessage = null;
  private static final HashMap<WebSocket, Client> playerClients = new HashMap<>();
  private static final HashMap<WebSocket, Client> spectatorClients = new HashMap<>();
  private static final MessageHandler messageHandler = new MessageHandler();
  private final LinkedBlockingDeque<Message> outgoingMessageQueue = new LinkedBlockingDeque<>();
  private boolean sendMessages = true;
  private Integer pauseClientID;
  private boolean pauseOvertime;
  private Timer pauseTimer;


  //endregion
  private MessageHandler() {
    new Thread(() -> {
      try {
        messageQueueThread();
      } catch (InterruptedException e) {
        sendMessages = false;
        Thread.currentThread().interrupt();
        throw new ServerErrorException(e.getMessage());
      }
    }).start();
  }

  public void processMessage(String s, WebSocket webSocket) {
    try {
      if (isInitial()) {
        MessageParser.parseMessage(s).ifPresentOrElse(message -> processInit(message, webSocket),
            () -> sendError(webSocket, 1));
      } else {
        MessageParser.parseMessage(s)
            .ifPresentOrElse(message -> process(message, webSocket),
                () -> {
                  outgoingMessageQueue.clear();
                  sendStrike(getClientByWebsocket(webSocket).getClientID(),
                      "Couldn't read message");
                });
      }
    } catch (Exception e) {
      logger.severe(e.getMessage());
      logger.severe(Arrays.toString(e.getStackTrace()));
      sendError(webSocket, 5);
    }
  }

  //region Process States
  private void process(Message message, WebSocket s) {

    switch (message.type) {
      case JOIN -> processJoinMessage((JoinMessage) message, s);
      case REJOIN -> processReJoinMessage((ReJoinMessage) message, s);
      case GAMESTATE_REQUEST -> {
        if (Game.getGameInstance().getGameState() == GameState.HOUSECHOICE) {
          var gamestateRequestMessage = (GamestateRequestMessage) message;
          sendStrike(gamestateRequestMessage.clientID, ILLEGAL_MESSAGE);
        }
        processGamestateRequestMessage((GamestateRequestMessage) message, s);
      }
      default -> {
        switch (Game.getGameInstance().getGameState()) {
          case PAUSE -> processPauseState(message, s);
          case HOUSECHOICE -> processHouseChoiceState(message, s);
          case CHARACTERTURN -> processTurnState(message, s);
          case SANDSTORM, SANDWORM, CLONE, DUNE, END -> processNonPlayerTurns(s);
          case GAME_END -> s.send("Game has ended!");
          default -> logger.severe(CONST.SHOULD_NOT_REACH_HERE);
        }
      }
    }
  }

  private void processNonPlayerTurns(WebSocket s) {
    sendStrike(getClientByWebsocket(s).getClientID(), ILLEGAL_MESSAGE);
  }

  private void processPauseState(Message message, WebSocket s) {
    var client = getClientByWebsocket(s);
    if (client == null) {
      s.close();
      return;
    }
    if (message.type != MessageType.PAUSE_REQUEST) {
      sendStrike(client.getClientID(), "Game is still paused!");
      return;
    }
    var pauseMessage = (PauseRequestMessage) message;
    if (pauseMessage.pause) {
      sendStrike(client.getClientID(), "Game is already paused!");

    } else if (pauseOvertime || client.getClientID() == pauseClientID) {
      stopPauseTimer();
      Game.getGameInstance().resumeGame();
      addMessageToQueue(new GamePauseDemandMessage(client.getClientID(), false));
    } else {
      sendStrike(client.getClientID(), "You cannot unpause the game!");
    }
  }

  private void processHouseChoiceState(Message message, WebSocket s) {
    if (message.type == MessageType.HOUSE_REQUEST) {
      processHouseRequestMessage((HouseRequestMessage) message, getClientByWebsocket(s));
    } else {
      outgoingMessageQueue.clear();
      sendStrike(getClientByWebsocket(s).getClientID(), ILLEGAL_MESSAGE);
    }
  }

  private void processTurnState(Message message, WebSocket s) {
    Optional<Integer> clientID = MessageParser.getClientIDFromMessage(message);
    if (clientID.isPresent() && clientID.get() != getClientByWebsocket(s).getClientID()) {
      outgoingMessageQueue.clear();
      sendStrike(getClientByWebsocket(s).getClientID(), "Fraud attempt!");
      return;
    }
    if (clientID.isEmpty()) {
      return;
    }
    switch (message.type) {
      case ACTION_REQUEST -> processActionRequestMessage((ActionRequestMessage) message);
      case MOVEMENT_REQUEST -> processMovementRequestMessage((MovementRequestMessage) message);
      case TRANSFER_REQUEST -> processTransferRequestMessage((TransferRequestMessage) message);
      case HELI_REQUEST -> processHeliRequestMessage((HeliRequestMessage) message); //Heliport
      case END_TURN_REQUEST -> processEndTurnRequestMessage((EndTurnRequestMessage) message);
      case PAUSE_REQUEST ->
          processPauseRequestMessage((PauseRequestMessage) message, clientID.get());
      case DEBUG -> processDebugMessage((DebugMessage) message);
      default -> {
        outgoingMessageQueue.clear();
        sendStrike(getClientByWebsocket(s).getClientID(), ILLEGAL_MESSAGE);
      }
    }
  }
  //endregion

  //region Process Messages
  private void processHouseRequestMessage(HouseRequestMessage message, Client client) {
    var player = getPlayerByClientID(client.getClientID());

    if (player == null) {
      outgoingMessageQueue.clear();
      sendStrike(client.getClientID(), SPECTATOR_NOT_ALLOWED_EXCEPTION);
      return;
    }

    try {
      Game.getGameInstance().checkHouseRequest(message.houseName, player);
    } catch (Exception e) {
      outgoingMessageQueue.clear();
      sendStrike(client.getClientID(), e.getMessage());
      return;
    }

    if (Game.getGameInstance().isHouseOfferFinished()) {
      Game.getGameInstance().setGameState(GameState.DUNE);
    }
  }

  private void processMovementRequestMessage(MovementRequestMessage message) {
    if (checkActionMovementRequestValidity(message.clientID, message.characterID)) {
      return;
    }

    try {
      Game.getGameInstance().checkMovement(message.characterID, message.specs);
    } catch (Exception e) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, e.getMessage());
    }
  }

  //Heliport
  private void processHeliRequestMessage(HeliRequestMessage message) {
    if (checkActionMovementRequestValidity(message.clientID, message.characterID)) {
      return;
    }

    try {
      Game.getGameInstance().checkHeli(message.characterID, message.target);
    } catch (Exception e) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, e.getMessage());
    }
  }

  private void processActionRequestMessage(ActionRequestMessage message) {
    if (checkActionMovementRequestValidity(message.clientID, message.characterID)) {
      return;
    }

    try {
      Game.getGameInstance().checkAction(message.characterID, message.action, message.specs);
    } catch (Exception e) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, e.getMessage());
    }
  }

  private boolean checkActionMovementRequestValidity(int message, int message1) {
    var player = getPlayerByClientID(message);

    if (player == null) {
      outgoingMessageQueue.clear();
      sendStrike(message, SPECTATOR_NOT_ALLOWED_EXCEPTION);
      return true;
    }

    if (Game.getGameInstance().checkNotCurrentTurn(message, message1)) {
      outgoingMessageQueue.clear();
      sendStrike(message, NOT_ALLOWED_TO_DO_ANYTHING_EXCEPTION);
      return true;
    }
    return false;
  }

  private void processTransferRequestMessage(TransferRequestMessage message) {
    if (Game.getGameInstance().checkNotCurrentTurn(message.clientID, message.characterID)) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, NOT_ALLOWED_TO_DO_ANYTHING_EXCEPTION);
      return;
    }

    try {
      Game.getGameInstance().checkTransfer(message.characterID, message.targetID, message.amount);
    } catch (Exception e) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, e.getMessage());
    }
  }

  private void processEndTurnRequestMessage(EndTurnRequestMessage message) {
    if (Game.getGameInstance().checkNotCurrentTurn(message.clientID, message.characterID)) {
      outgoingMessageQueue.clear();
      sendStrike(message.clientID, NOT_ALLOWED_TO_DO_ANYTHING_EXCEPTION);
      return;
    }
    GameTimer.stopTimer();
    Game.getGameInstance().checkHeal();
    if (!Game.getGameInstance().nextCharacter()) {
      Game.getGameInstance().setGameState(GameState.END);
    }
  }

  private void processGamestateRequestMessage(GamestateRequestMessage message, WebSocket s) {
    List<Client> clients = new ArrayList<>(playerClients.values());
    s.send(new GamestateMessage(message.clientID,
        clients.stream().mapToInt(Client::getClientID).boxed().collect(Collectors.toList()),
        Game.getGameInstance().getCurrentGameStateHistory()).toString());
  }

  private void processPauseRequestMessage(PauseRequestMessage message, int clientID) {
    if (message.pause) {
      Game.getGameInstance().setGameState(GameState.PAUSE);
      addMessageToQueue(new GamePauseDemandMessage(clientID, true));
      pauseClientID = clientID;
      startPauseTimer();
    } else {
      sendStrike(clientID, "Game is not paused!");
    }
  }

  private void processDebugMessage(DebugMessage message) {
    logger.log(Level.INFO, "{0}", message);
  }

  private void processInit(Message message, WebSocket s) {
    if (Game.getGameInstance() == null) {
      Game.createGame();
    }
    if (message.type == MessageType.JOIN) {
      processJoinMessage((JoinMessage) message, s);
    } else {
      sendError(s, 5);
    }
  }

  private void processReJoinMessage(ReJoinMessage message, WebSocket s) {
    var client = getClientByClientSecret(message.clientSecret);
    if (client == null) {
      sendError(s, 4);
    } else {
      if (client.getPlayer() == null) {
        spectatorClients.remove(client.getClientWebSocket());
        client.setClientWebSocket(s);
        spectatorClients.put(s, client);
      } else {
        playerClients.remove(client.getClientWebSocket());
        client.setClientWebSocket(s);
        playerClients.put(s, client);
      }
      s.send(new JoinAcceptedMessage(client.getClientSecret(), client.getClientID()).toString());
    }
  }

  private void processJoinMessage(JoinMessage message, WebSocket s) {
    Client client;
    if (containsWebsocket(s)) {
      sendError(s, 5);
      return;
    }
    if (message.isActive) {
      if (Game.getGameInstance().canBePlayer()) {
        client = addPlayer(message, s);
      } else {
        sendError(s, 3);
        return;
      }
    } else {
      if (!message.isCpu) {
        client = addSpectator(s, message);
      } else {
        sendError(s, 2);
        return;
      }
    }

    sendJoinAck(s, client);
    checkGameConfigSend(s);
  }

  private void sendStrike(int clientID, String wrongMessage) {
    var client = getClientByClientID(clientID);
    if (client == null) {
      return;
    }

    if (client.strike()) {
      addMessageToQueue(new StrikeMessage(clientID, wrongMessage, client.getStrikeCount()));
      return;
    } else {
      try {
        var webSocket = client.getClientWebSocket();
        if (webSocket == null) {
          logger.severe("Client has not Websocket assigned!");
          return;
        }
        webSocket.close();
      } catch (Exception e) {
        logger.severe("No Websocket for closure found!");
      }
    }

    if (playerClients.containsValue(getClientByClientID(clientID))) {
      var winnerID = 0;

      for (Client otherClient : playerClients.values()) {
        if (otherClient.getClientID() != clientID) {
          winnerID = otherClient.getClientID();
          break;
        }
      }

      addMessageToQueue(new GameEndMessage(winnerID, clientID, null));
      Game.getGameInstance().setGameState(GameState.GAME_END);
    }
  }

  private void sendError(WebSocket s, int errorCode) {
    switch (errorCode) {
      case 1 -> s.send(new ErrorMessage(errorCode, "Bad format.").toString());
      case 2 -> s.send(new ErrorMessage(errorCode, "isCpu and isActive do not match.").toString());
      case 3 -> s.send(new ErrorMessage(errorCode, "Already two players registered.").toString());
      case 4 -> s.send(new ErrorMessage(errorCode, "Unknown clientSecret").toString());
      case 5 -> s.send(new ErrorMessage(errorCode, "An unknown error occurred").toString());
      default ->
          s.send(new ErrorMessage(errorCode, "An unknown unknown error occurred").toString());
    }
  }

//endregion

  //region Getter
  private Player getPlayerByClientID(int clientID) {
    Player player = null;
    for (Client c : playerClients.values()
    ) {
      if (c.getClientID() == clientID) {
        player = c.getPlayer();
        break;
      }
    }
    return player;
  }

  private Client getClientByClientID(int clientID) {
    for (Client client : playerClients.values()) {
      if (client.getClientID() == clientID) {
        return client;
      }
    }

    for (Client client : spectatorClients.values()) {
      if (client.getClientID() == clientID) {
        return client;
      }
    }

    return null;
  }

  private Client getClientByClientSecret(String clientSecret) {
    for (Client client : playerClients.values()) {
      if (Objects.equals(client.getClientSecret(), clientSecret)) {
        return client;
      }
    }
    for (Client client : spectatorClients.values()) {
      if (Objects.equals(client.getClientSecret(), clientSecret)) {
        return client;
      }
    }
    return null;
  }


  private Client getClientByWebsocket(WebSocket webSocket) {
    Client client;

    client = playerClients.get(webSocket);

    if (client != null) {
      return client;
    }

    return spectatorClients.get(webSocket);
  }

  public static MessageHandler getInstance() {
    return messageHandler;
  }
  //endregion

  //region Setter
  public void setServer(ServerMain server) {
    serverMain = server;
  }
  //endregion

  //region Send Messages

  private void sendJoinAck(WebSocket s, Client client) {
    s.send(new JoinAcceptedMessage(client.getClientSecret(), client.getClientID()).toString());
  }

  private void sendGameConfig(WebSocket s) {
    s.send(createGameConfig().toString());
  }

  public void sendTurnDemand(int clientID, int characterID) {
    addMessageToQueue(new TurnDemandMessage(clientID, characterID));
  }
  //endregion

  private Client addPlayer(JoinMessage message, WebSocket s) {
    var player = new Player(message.clientName, message.isCpu);
    Game.getGameInstance().addPlayer(player);
    var client = createClient(s, message.clientName, player, message.isCpu);
    playerClients.put(s, client);
    return client;
  }

  private Client addSpectator(WebSocket s, JoinMessage message) {
    var client = createClient(s, message.clientName, message.isCpu);
    spectatorClients.put(s, client);
    return client;
  }


  //Sends init GameConfig to all clients when both players are connected
  public void gameStart() {
    Game.getGameInstance().startNewGame();
    serverMain.broadcast(createGameConfig().toString());
    Game.getGameInstance().setGameState(GameState.HOUSECHOICE);
  }

  private Client createClient(WebSocket s, String clientName, boolean isCPU) {
    return createClient(s, clientName, null, isCPU);
  }

  private Client createClient(WebSocket s, String clientName, Player player, boolean isCPU) {
    var clientSecret = java.util.UUID.randomUUID().toString();
    CONST.logger.info(clientSecret);
    return new Client(clientSecret, clientName, s, player, isCPU);
  }


  private void checkGameConfigSend(WebSocket s) {
    if (Game.getGameInstance().isIngame()) {
      sendGameConfig(s);
    } else if (playerClients.size() >= 2) {
      gameStart();
    }
  }

  private void messageQueueThread() throws InterruptedException {
    while (sendMessages) {
      var message = outgoingMessageQueue.take();
      serverMain.broadcast(message.toString());
    }
  }

  private boolean containsWebsocket(WebSocket s) {
    if (playerClients.containsKey(s)) {
      return true;
    } else {
      return spectatorClients.containsKey(s);
    }
  }

  public void addMessageToQueue(Message message) {
    try {
      outgoingMessageQueue.put(message);
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  private boolean isInitial() {
    return (Game.getGameInstance() == null ||
        !Game.getGameInstance().isIngame());

  }

  private static GamecfgMessage createGameConfig() {
    if (gamecfgMessage == null) {
      var cityToClients = new CityToClient[2];
      var i = 0;
      for (Client client : playerClients.values()
      ) {
        System.out.println("CLIENT NAME1111: "+ client.getClientName());
        cityToClients[i] = new CityToClient(client.getClientID(), client.getClientName(),
            client.getPlayer().getCityPosition());
        System.out.println("CITIESTOCLIENTS: " + cityToClients[i].toString());
        i++;
      }
      gamecfgMessage = new GamecfgMessage(ScenarioConfig.getCurrentConfig(),
          PartyConfig.getCurrentConfig(), cityToClients, Game.getGameInstance().getEyeOfTheStorm());
    }
    return gamecfgMessage;
  }

  public void sendGameEnd(int clientIDWinner, int clientIDLoser) {
    addMessageToQueue(new GameEndMessage(clientIDWinner, clientIDLoser, null));
  }

  private void startPauseTimer() {
    pauseOvertime = false;
    pauseTimer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        pauseOvertime = true;
        addMessageToQueue(new UnpauseGameOfferMessage(pauseClientID));
      }
    };
    pauseTimer.schedule(task, PartyConfig.getCurrentConfig().minPauseTime); // * 1000L * 60L
  }

  private void stopPauseTimer() {
    if (pauseTimer != null) {
      try {
        pauseTimer.cancel();
        pauseTimer.purge();
      } catch (Exception ignored) {
        logger.info("Timer is already stopped!");
      }
    }
  }

  public boolean isMessageQueueEmpty() {
    return outgoingMessageQueue.isEmpty();
  }
}

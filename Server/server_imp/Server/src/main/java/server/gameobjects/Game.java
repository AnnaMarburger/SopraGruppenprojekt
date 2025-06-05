package server.gameobjects;

import java.util.*;

import static shared_data.CONST.*;

import exceptions.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import messages.*;
import messages.data.ChangeReason;
import messages.data.character.MessageCharacter;
import messages.data.dictionary.ActionSpecs;
import messages.data.dictionary.Attributes;
import messages.data.dictionary.Stats;
import messages.data.dictionary.MovementSpecs;
import server.MessageHandler;
import server.ServerMain;
import server.gameobjects.gameboard.character.*;
import shared_data.Action;
import shared_data.Position;
import shared_data.config.PartyConfig;
import shared_data.config.ScenarioConfig;
import server.gameobjects.gameboard.GameBoard;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.GameTileType;
import shared_data.house.House;
import shared_data.house.HouseAtreides;
import shared_data.house.HouseCorrino;
import shared_data.house.HouseHarkonnen;
import shared_data.house.HouseName;
import shared_data.house.HouseOrdos;
import shared_data.house.HouseRichese;
import shared_data.house.HouseVernius;

/**
 * This class represents the game logic. Handles almost everything.
 */
public class Game {

  private final ActionHandler actionHandler = new ActionHandler(this);
  private final MovementHandler movementHandler = new MovementHandler(this);

  private final HeliHandler heliHandler = new HeliHandler(this);
  private final HouseRequestHandler houseRequestHandler = new HouseRequestHandler(this);
  private final PhaseHandler phaseHandler = new PhaseHandler(this);
  private boolean atomicHit = false;
  private boolean atomicHelp = true;
  private int maxRounds;
  private int currRound = 1;
  private boolean endGame = false;
  private GameBoard gameBoard;
  private boolean ingame = false;
  private GameState gameState;
  private int currPlayer = 0;
  private final Player[] players;
  private static Game gameInstance;
  private List<House> houseSelectionPlayer0;
  private List<House> houseSelectionPlayer1;
  private final Queue<CharacterController> characterQueue = new LinkedBlockingDeque<>();
  private CharacterController currentCharacter = null;
  private WormController wormController;
  //endregion

  private Game() {
    players = new Player[2];
  }

  public void addPlayer(Player player) {
    if (players[0] == null) {
      players[0] = player;
    } else if (players[1] == null) {
      players[1] = player;
    }
  }

  public boolean canBePlayer() {
    return (players[0] == null || players[1] == null);
  }

  //region Checks

  public boolean checkNotCurrentTurn(int clientID, int characterID) {
    return currentCharacter.getCharacterID() != characterID
        || getCurrentPlayer().getClientID() != clientID;
  }

  public void checkHouseRequest(HouseName houseName, Player player)
      throws InvalidPlayerNumberException, InvalidHouseSelectionException {

    houseRequestHandler.checkHouseRequest(houseName, player);
  }
  public PhaseHandler getPhaseHandler(){
    return this.phaseHandler;
  }
  public void checkAction(int characterID, Action action, ActionSpecs specs)
      throws UnableToExecuteActionException, OutOfRangeException, InconsistentDataException, ShouldNotReachHereException {

    actionHandler.checkAction(characterID, action, specs);
  }

  public void checkMovement(int characterID, MovementSpecs specs) throws
      InconsistentDataException {
    movementHandler.checkMovement(characterID, specs);
  }

  //Heliport
  public void checkHeli(int characterID, Position target) throws
          InconsistentDataException {
    heliHandler.checkHeli(characterID, target);
  }

  public void checkTransfer(int sourceCharacterID, int targetCharacterID, int amount)
      throws UnableToExecuteActionException, InconsistentDataException, OutOfRangeException {
    actionHandler.checkTransfer(sourceCharacterID, targetCharacterID, amount);
  }

  public void checkHeal() {
    if (currentCharacter.tryHeal()) {
      MessageHandler.getInstance().addMessageToQueue(new CharacterStatChangeDemandMessage(
          getCurrentPlayer().getClientID(),
          currentCharacter.getCharacterID(),
          currentCharacter.getStats()));
    }
  }

  public boolean isHouseOfferFinished() {
    for (Player player : players) {
      if (player.getHouseName() == null) {
        return false;
      }
    }

    return true;
  }
  //endregion

  //region GameStart
  public static void createGame() {
    gameInstance = new Game();
  }

  private void init() {
    gameInstance.loadPartyConfig(PartyConfig.getCurrentConfig());
    gameInstance.loadScenarioConfig(ScenarioConfig.getCurrentConfig());
    maxRounds = PartyConfig.getCurrentConfig().numbOfRounds;
    ingame = true;
    wormController = new WormController(this.gameBoard);
    houseRequestHandler.assignCityToPlayer();
  }

  public void startNewGame() {
    init();
  }

  private void houseOffer() {
    //choose 4 out of 6 houses and send each player 2 of them -> house offer
    List<House> houseSelection = selectRandomHouses();

    houseSelectionPlayer0 = new ArrayList<>(Arrays.asList(houseSelection.get(0),
        houseSelection.get(1)));
    houseSelectionPlayer1 = new ArrayList<>(Arrays.asList(houseSelection.get(2),
        houseSelection.get(3)));

    MessageHandler.getInstance()
        .addMessageToQueue(
            new HouseOfferMessage(players[0].getClientID(), houseSelectionPlayer0));
    MessageHandler.getInstance()
        .addMessageToQueue(
            new HouseOfferMessage(players[1].getClientID(), houseSelectionPlayer1));
  }

  private List<House> selectRandomHouses() {
    List<HouseName> houseNameSelection = new ArrayList<>();
    List<House> houseSelection = new ArrayList<>();

    while (houseNameSelection.size() < 4) {
      var newHouseName = HouseName.randomHouseName();

      if (!houseNameSelection.contains(newHouseName)) {
        houseNameSelection.add(newHouseName);
      }
    }

    for (HouseName houseName : houseNameSelection) {
      switch (houseName) {
        case CORRINO -> houseSelection.add(new HouseCorrino());
        case ATREIDES -> houseSelection.add(new HouseAtreides());
        case HARKONNEN -> houseSelection.add(new HouseHarkonnen());
        case ORDOS -> houseSelection.add(new HouseOrdos());
        case RICHESE -> houseSelection.add(new HouseRichese());
        case VERNIUS -> houseSelection.add(new HouseVernius());
      }
    }

    return houseSelection;
  }

  //endregion

  List<Position> generateSpawningPositions(Player player) {
    List<Position> spawningPositions = new ArrayList<>();
    var cityPosition = player.getCityPosition();
    List<GameTile> gameTileList = gameBoard.getTilesInRadiusWithMinDistance(6, cityPosition, 0);
    for (GameTile t : gameTileList
    ) {
      if (t.getAccessibility() && !t.isOccupied()) {
        spawningPositions.add(new Position(t.getPosition()));
        if (spawningPositions.size() == 6) {
          break;
        }
      }
    }

    return spawningPositions;
  }

  CharacterController constructCharacterController(MessageCharacter messageCharacter,
      Position position) {
    switch (messageCharacter.characterClass) {
      case NOBLE -> {
        return new CharacterController(new NobleGameCharacter(position,
            messageCharacter.characterName), gameBoard);
      }
      case MENTAT -> {
        return new CharacterController(new MentatGameCharacter(position,
            messageCharacter.characterName), gameBoard);
      }
      case BENE_GESSERIT -> {
        return new CharacterController(new BeneGesseritGameCharacter(position,
            messageCharacter.characterName), gameBoard);
      }
      case FIGHTER -> {
        return new CharacterController(new FighterGameCharacter(position,
            messageCharacter.characterName), gameBoard);
      }
      default -> {
        return null;
      }
    }
  }

  boolean isTargetInRange(Position position, Position target) {
    return target.x >= position.x - 1 && target.x <= position.x + 1
        && target.y >= position.y - 1 && target.y <= position.y + 1;
  }

  public boolean nextCharacter() {
    while (!characterQueue.isEmpty()) {
      currentCharacter = characterQueue.poll();
      if (currentCharacter.isDead() || !currentCharacter.isCloneable()) {
        currentCharacter = null;
        continue;
      }

      var player = getPlayerByCharacterController(currentCharacter);

      if (player == null) {
        throw new NullPointerException("Did not find player for given character!");
      }
      currPlayer = player.getPlayerNumber();
      if (currentCharacter.resetCharacter()) {
        MessageHandler.getInstance().addMessageToQueue(
            new CharacterStatChangeDemandMessage(players[currPlayer].getClientID(),
                currentCharacter.getCharacterID(), currentCharacter.getStats())
        );
      }
      MessageHandler.getInstance().sendTurnDemand(players[currPlayer].getClientID(),
          currentCharacter.getCharacterID());

      resetTimer(players[currPlayer].isCpu());
      return true;
    }
    return false;
  }

  void checkIfTurnEnd(int characterID) {
    var characterController = getCharacterControllerByPlayerAndCharacterID(
        getCurrentPlayer(), characterID);

    if (characterController == null) {
      throw new NullPointerException(CHARACTER_ID_EXCEPTION);
    }

    if (characterController.isTurnFinished() && nextCharacter()) {
      setGameState(GameState.END);
    }
  }

  public boolean checkPlayerCitySpiceTransfer(int characterID) {
    var cityPosition = getCurrentPlayer().getCityPosition();
    var characterController = getCharacterControllerByPlayerAndCharacterID(
        getCurrentPlayer(), characterID);

    if (characterController == null) {
      throw new NullPointerException(CHARACTER_ID_EXCEPTION);
    }

    var characterPosition = characterController.getPosition();

    return isTargetInRange(characterPosition, cityPosition);
  }

  private void gameEnd() {
    while (!MessageHandler.getInstance().isMessageQueueEmpty()) {
      //Shall be empty! (wait)
    }
    ServerMain.closeServer();
  }

  private void resetTimer(boolean cpu) {
    TimerTask timeout = new TimerTask() {
      @Override
      public void run() {
        if (!nextCharacter()) {
          setGameState(GameState.END);
        }
      }
    };
    if (cpu) {
      GameTimer.startTimer(timeout, PartyConfig.getCurrentConfig().actionTimeAiClient);
    } else {
      GameTimer.startTimer(timeout, PartyConfig.getCurrentConfig().actionTimeUserClient);
    }
  }

  private void pauseGame() {
    GameTimer.stopTimer();
  }

  public void resumeGame() {
    gameState = GameState.CHARACTERTURN;
    resetTimer(players[currPlayer].isCpu());
  }

  //region Send Message
  void sendCharacterStatChangeDemandMessage(int clientID, int characterID, Stats stats) {
    MessageHandler.getInstance().addMessageToQueue(
        new CharacterStatChangeDemandMessage(clientID, characterID, stats));
  }

  void sendActionDemandMessage(int clientID, int characterID, Action action,
      ActionSpecs specs) {
    MessageHandler.getInstance().addMessageToQueue(
        new ActionDemandMessage(clientID, characterID, action, specs));
  }

  void sendMapChangeDemandMessage(ChangeReason changeReason) {
    MessageHandler.getInstance().addMessageToQueue(
        new MapChangeDemandMessage(changeReason, gameBoard.convertMapToMessageFormat(),
            gameBoard.getEyeOfTheStorm()));
  }

  void sendTransferDemandMessage(int clientID, int characterID, int targetID) {
    MessageHandler.getInstance().addMessageToQueue(
        new TransferDemandMessage(clientID, characterID, targetID));
  }

  void sendChangePlayerSpiceDemand(int clientID, int spiceValue) {
    MessageHandler.getInstance()
        .addMessageToQueue(new ChangePlayerSpiceDemandMessage(clientID, spiceValue));
  }

  void sendSpawnCharacterDemandMessage(int clientID, int characterID, String characterName, Position position,
      Attributes attributes) {
    MessageHandler.getInstance().addMessageToQueue(new SpawnCharacterDemandMessage(clientID,
        characterID, characterName, position, attributes));
  }
  //endregion

  //region Getter
  public Position getEyeOfTheStorm() {
    return gameBoard.getEyeOfTheStorm();
  }

  public static Game getGameInstance() {
    return gameInstance;
  }

  public GameState getGameState() {
    return gameState;
  }

  public Player getCurrentPlayer() {
    return players[currPlayer];
  }

  public Player getOppositePlayer() {
    return getCurrentPlayer().getPlayerNumber() == 0 ? players[1] : players[0];
  }

  public boolean isIngame() {
    return ingame;
  }

  public int getHeightForPosition(Position position) {
    return gameBoard.getMap()[position.y][position.x].getHeight();
  }

  CharacterController getCharacterControllerByPlayerAndCharacterID(Player player,
      int characterID) {
    CharacterController characterController = null;

    for (CharacterController c : player.getCharacterControllers()) {
      if (c.getCharacterID() == characterID) {
        characterController = c;
      }
    }

    return characterController;
  }

  CharacterController getCharacterControllerByPosition(Position position) {
    if (!gameBoard.getMap()[position.y][position.x].isOccupied()) {
      return null;
    }

    for (Player player : players) {
      for (CharacterController characterController : player.getCharacterControllers()) {
        if (characterController.getPosition().equals(position)) {
          return characterController;
        }
      }
    }

    return null;
  }

  Player getPlayerByCharacterController(CharacterController controller) {
    for (Player player : players
    ) {
      if (player.getCharacterControllers().contains(controller)) {
        return player;
      }
    }
    return null;
  }

  public CharacterController getCurrentCharacter() {
    return currentCharacter;
  }

  public GameBoard getGameBoard() {
    return gameBoard;
  }

  public WormController getWormController() {
    return wormController;
  }

  public boolean isAtomicHit() {
    return atomicHit;
  }

  List<House> getHouseSelectionPlayer0() {
    return houseSelectionPlayer0;
  }

  List<House> getHouseSelectionPlayer1() {
    return houseSelectionPlayer1;
  }

  public boolean isEndGame() {
    return endGame;
  }

  public int getCurrRound() {
    return currRound;
  }

  public int getMaxRounds() {
    return maxRounds;
  }

  public boolean isAtomicHelp() {
    return atomicHelp;
  }

  public Player[] getPlayers() {
    return players;
  }

  public Queue<CharacterController> getCharacterQueue() {
    return characterQueue;
  }

  public List<String> getCurrentGameStateHistory() {
    List<String> history = new ArrayList<>();
    history.add(new MapChangeDemandMessage(ChangeReason.ROUND_PHASE,
        gameBoard.convertMapToMessageFormat(), gameBoard.getEyeOfTheStorm()).toString());
    var map = getAliveCharacters();
    for (Entry<CharacterController, Integer> entry : map.entrySet()) {
      history.add(
          new SpawnCharacterDemandMessage(entry.getValue(), entry.getKey().getCharacterID(), entry.getKey().getCharacterName(),
              entry.getKey().getPosition(), entry.getKey().getAttributes()).toString());
    }
    for (Player player : players) {
      history.add(
          new ChangePlayerSpiceDemandMessage(player.getClientID(), player.getSpice()).toString());
    }
    if (wormController.isActive()) {
      history.add(new SandwormSpawnDemandMessage(wormController.getTargetClientID(),
          wormController.getTarget().getId(),
          wormController.getPosition()).toString());
    }
    if (endGame) {
      history.add(new EndgameMessage().toString());
    }
    history.add(new TurnDemandMessage(getCurrentPlayer().getClientID(),
        currentCharacter.getCharacterID()).toString());
    return history;
  }

  HashMap<CharacterController, Integer> getAliveCharacters() {
    HashMap<CharacterController, Integer> characterList = new HashMap<>();
    for (Player player : players) {
      for (CharacterController controller : player.getCharacterControllers()) {
        if (!controller.isDead()) {
          characterList.put(controller, player.getClientID());
        }
      }
    }
    return characterList;
  }
  //endregion

  //region Setter
  public void setGameState(GameState gameState) {
    logger.log(Level.INFO, "Changed to: {0}", gameState);
    if (this.gameState == GameState.GAME_END) {
      return;
    }
    this.gameState = gameState;
    switch (gameState) {
      case HOUSECHOICE -> houseOffer();
      case DUNE -> phaseHandler.startDunePhase();
      case SANDSTORM -> phaseHandler.startSandstormPhase();
      case SANDWORM -> {
        if (endGame) {
          phaseHandler.startShaiHuludPhase();
        } else {
          phaseHandler.startSandwormPhase();
        }
      }
      case CLONE -> phaseHandler.startClonePhase();
      case CHARACTERTURN -> phaseHandler.startCharacterTurnPhase();
      case END -> phaseHandler.startEndPhase();
      case GAME_END -> gameEnd();
      case PAUSE -> pauseGame();
    }
  }

  public void loadPartyConfig(PartyConfig partyConfig) {
    PartyConfig.setCurrentConfig(partyConfig);
  }

  public void loadScenarioConfig(ScenarioConfig scenarioConfig) {
    var map = new GameTile[scenarioConfig.scenario.length][scenarioConfig.scenario[0].length];
    for (var y = 0; y < map.length; y++) {
      for (var x = 0; x < map[0].length; x++) {
        map[y][x] = new GameTile(new Position(x, y),
            GameTileType.valueOf(String.valueOf(scenarioConfig.scenario[y][x]))
        );
      }
    }

    gameBoard = new GameBoard(scenarioConfig.scenario.length, scenarioConfig.scenario[0].length,
        map);
  }

  public void setCurrPlayer(int playerNumber) {
    currPlayer = playerNumber;
  }

  public void setAtomicHelp(boolean atomicHelp) {
    this.atomicHelp = atomicHelp;
  }

  public void setEndGame(boolean endGame) {
    this.endGame = endGame;
  }

  public void setCurrRound(int round) {
    this.currRound = round;
  }

  public void setAtomicHit(boolean atomicHit) {
    this.atomicHit = atomicHit;
  }
  //endregion
}

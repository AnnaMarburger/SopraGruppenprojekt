package server.gameobjects;

import static shared_data.CONST.logger;
import static shared_data.CONST.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import messages.EndgameMessage;
import messages.data.ChangeReason;
import messages.data.character.MessageCharacter;
import server.MessageHandler;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.character.CharacterController;
import shared_data.CONST;
import shared_data.Position;
import shared_data.config.PartyConfig;
import shared_data.house.HouseCharacterMap;
import shared_data.house.HouseName;

public class PhaseHandler {

  private final Game game;
  private final WormPhaseHandler wormPhaseHandler = new WormPhaseHandler(this);

  public PhaseHandler(Game game) {
    this.game = game;
  }

  //region Dune Phase
  void startDunePhase() {
    game.getGameBoard().checkSpiceBlow();
    game.getGameBoard().duneWalk();
    game.sendMapChangeDemandMessage(ChangeReason.ROUND_PHASE);
    game.setGameState(GameState.SANDSTORM);
    CONST.logger.info("Successfully executed dune phase");
  }
  //endregion

  //region Sandstorm Phase
  void startSandstormPhase() {
    game.getGameBoard().moveSandStorm();
    game.sendMapChangeDemandMessage(ChangeReason.SANDSTORM);
    game.setGameState(GameState.SANDWORM);
    CONST.logger.info("Successfully executed sandstorm phase");
  }
  //endregion

  //region Sandworm Phase
  void startSandwormPhase() {
    wormPhaseHandler.startSandwormPhase();
  }

  public WormPhaseHandler getWormPhaseHandler(){
    return this.wormPhaseHandler;
  }

  void startShaiHuludPhase() {
    wormPhaseHandler.startShaiHuludPhase();
  }
  //endregion

  //region Clone Phase
  void startClonePhase() {
    cloneCharacters();
    game.setGameState(GameState.CHARACTERTURN);
    CONST.logger.info("Successfully executed clone phase");
  }

  private void cloneCharacters() {
    List<CharacterController> cloneableCharacters;
    List<Position> positionList;
    for (Player player : game.getPlayers()) {
      positionList = game.generateSpawningPositions(player);
      cloneableCharacters = player.getCloneableCharacters();
      for (CharacterController cloneableCharacter : cloneableCharacters
      ) {
        if (cloneableCharacter.isDead()) {
          tryClone(cloneableCharacter, positionList.remove(0), player.getClientID());
        }
      }
    }
  }

  private void tryClone(CharacterController characterController, Position pos, int clientID) {

    if (PartyConfig.getCurrentConfig().cloneProbability > random.nextFloat()) {
      characterController.resetCharacter();
      characterController.spawnAt(pos);

      game.sendSpawnCharacterDemandMessage(clientID, characterController.getCharacterID(), characterController.getCharacterName(),
          characterController.getPosition(), characterController.getAttributes());
    }
  }
  //endregion

  //region Character Turn Phase
  void startCharacterTurnPhase() {
    createTurnOrder();
    if (!game.nextCharacter()) {
      game.setGameState(GameState.END);
    }
    CONST.logger.info("Successfully executed turn phase");
  }

  private void createTurnOrder() {
    List<CharacterController> tmpList = new ArrayList<>();
    for (Player player : game.getPlayers()) {
      tmpList.addAll(player.getCharacterControllers());
    }
    Collections.shuffle(tmpList);
    game.getCharacterQueue().addAll(tmpList);
  }
  //endregion

  //region End Phase
  public void startEndPhase() {
    System.out.println("-----------------ENDGAME-----------------");
    if (!game.isEndGame() && (game.getCurrRound() > game.getMaxRounds())) {
      MessageHandler.getInstance().addMessageToQueue(new EndgameMessage());
      game.setEndGame(true);
      game.getGameBoard().changeToEndgameMap();
      game.sendMapChangeDemandMessage(ChangeReason.ENDGAME);
    }
    game.setCurrRound(game.getCurrRound() + 1);
    if (game.isAtomicHelp() && game.isAtomicHit()) {
      game.setAtomicHelp(false);
      generateFamilyHelp();
    }
    game.setGameState(GameState.DUNE);
  }

  private void generateFamilyHelp() {
    List<HouseName> houseNames = HouseName.getAtomicHelpHouseNames(
        game.getCurrentPlayer().getHouseName(), game.getOppositePlayer().getHouseName());
    List<GameTile> gameTiles = game.getGameBoard().twoDArrayToList(game.getGameBoard().getMap());

    Queue<MessageCharacter> messageCharacters = new LinkedList<>();

    for (HouseName houseName : houseNames) {
      messageCharacters.add(HouseCharacterMap.getInstance().getCharacterForHouse(houseName));
    }

    while (!messageCharacters.isEmpty()) {
      var messageCharacter = messageCharacters.poll();
      CharacterController tmpController;
      Optional<GameTile> tile = game.getGameBoard().getRandomTile(gameTiles,
          (y, x) -> !game.getGameBoard().getTile(new Position(x, y)).isOccupied());

      if (tile.isPresent()) {
        var position = tile.get().getPosition();
        tmpController = game.constructCharacterController(messageCharacter, position);

        var player = getShunnedPlayer();

        if (player == null) {
          logger.severe("No shunned player found!");
          return;
        }
        game.setCurrPlayer(player.getPlayerNumber());
        if (tmpController == null) {
          logger.severe("No character controller found!");
          return;
        }

        game.getOppositePlayer().getCharacterControllers().add(tmpController);

        game.sendSpawnCharacterDemandMessage(game.getOppositePlayer().getClientID(),
            tmpController.getCharacterID(), tmpController.getCharacterName(), tmpController.getPosition(),
            tmpController.getAttributes());
      } else {
        break;
      }
    }
  }

  private Player getShunnedPlayer() {
    for (Player player : game.getPlayers()) {
      if (player.isShunned()) {
        return player;
      }
    }
    return null;
  }
  //endregion

  public Game getGame() {
    return game;
  }
}
package server.gameobjects;

import static shared_data.character.CharacterType.FIGHTER;

import exceptions.InconsistentDataException;
import exceptions.OutOfRangeException;
import exceptions.ShouldNotReachHereException;
import exceptions.UnableToExecuteActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import messages.ActionDemandMessage;
import messages.AtomicsUpdateDemandMessage;
import messages.CharacterStatChangeDemandMessage;
import messages.data.ChangeReason;
import messages.data.dictionary.ActionSpecs;
import server.MessageHandler;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.GameTileType;
import server.gameobjects.gameboard.character.CharacterController;
import shared_data.Action;
import shared_data.CONST;
import shared_data.character.CharacterType;

public class ActionHandler {

  private final Game game;

  public ActionHandler(Game game) {
    this.game = game;
  }

  public void checkAction(int characterID, Action action, ActionSpecs specs)
      throws UnableToExecuteActionException, OutOfRangeException, InconsistentDataException, ShouldNotReachHereException {

    if (game.isTargetInRange(game.getCurrentCharacter().getPosition(), game.getEyeOfTheStorm())) {
      throw new UnableToExecuteActionException("Cannot execute Action, character is in Sandstorm!");
    }

    switch (action) {
      case ATTACK -> checkAndDoAttackAction(characterID, specs);
      case COLLECT -> checkAndDoCollectAction(characterID, specs);
      case KANLY -> checkAndDoKanlyAction(characterID, specs);
      case FAMILY_ATOMICS -> checkAndDoFamilyAtomicsAction(characterID, specs);
      case SPICE_HOARDING -> checkAndDoSpiceHoardingAction(characterID, specs);
      case VOICE -> checkAndDoVoiceAction(characterID, specs);
      case SWORDSPIN -> checkAndDoSwordSpinAction(characterID, specs);
      default -> throw new ShouldNotReachHereException();
    }

    CONST.logger.info("Successfully executed Action");
    game.checkIfTurnEnd(characterID);
  }

  void checkAndDoAttackAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException, OutOfRangeException {
    var attackerCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (attackerCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }

    if (attackerCharacterController.cannotDoNormalAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_NORMAL_EXCEPTION);
    }

    var targetCharacterController = game.getCharacterControllerByPosition(specs.target);

    if (targetCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_POSITION_EXCEPTION);
    }

    if (game.isTargetInRange(targetCharacterController.getPosition(), game.getEyeOfTheStorm())) {
      throw new UnableToExecuteActionException(CONST.CHARACTER_IN_SANDSTORM);
    }

    if (!game.isTargetInRange(attackerCharacterController.getPosition(), specs.target)) {
      throw new OutOfRangeException("Target character is not in reach of attacker!");
    }

    if (!Objects.equals(game.getPlayerByCharacterController(targetCharacterController),
        game.getOppositePlayer())) {
      throw new UnableToExecuteActionException("Cannot attack own characters!");
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID, Action.ATTACK,
        specs);

    attackerCharacterController.attack(targetCharacterController, specs.target);
    if (targetCharacterController.isDead()) {
      game.getCurrentPlayer()
          .setEnemiesKilledCounter(game.getCurrentPlayer().getEnemiesKilledCounter() + 1);
    }

    //add character stat change of attacker
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        attackerCharacterController.getStats());

    //add character stat change demand of target
    game.sendCharacterStatChangeDemandMessage(game.getOppositePlayer().getClientID(),
        targetCharacterController.getCharacterID(), targetCharacterController.getStats());

    CONST.logger.info("Successfully executed attack action!");
  }

  void checkAndDoCollectAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException, InconsistentDataException {
    var characterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (characterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }

    if (characterController.cannotDoNormalAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_NORMAL_EXCEPTION);
    }

    if (!characterController.getPosition().equals(specs.target)) {
      throw new InconsistentDataException();
    }

    if (!game.getGameBoard().getMap()[specs.target.y][specs.target.x].isSpice()) {
      throw new InconsistentDataException("No spice on specified field to collect!");
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID, Action.COLLECT,
        specs);

    //remove spice from field
    game.getGameBoard().getMap()[specs.target.y][specs.target.x].removeSpice();

    //add spice to inventory + increase spice pickup counter
    characterController.setSpice(characterController.getSpice() + 1);
    game.getCurrentPlayer()
        .setSpicePickupCounter(game.getCurrentPlayer().getSpicePickupCounter() + 1);

    //add map change demand message
    game.sendMapChangeDemandMessage(ChangeReason.SPICE_PICKUP);

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        characterController.getStats());

    CONST.logger.info("Successfully executed collect action!");
  }

  void checkAndDoKanlyAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException, OutOfRangeException {
    var attackerCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (attackerCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }

    if (attackerCharacterController.cannotDoSpecialAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_SPECIAL_EXCEPTION);
    }

    if (attackerCharacterController.getCharacterType() != CharacterType.NOBLE) {
      throw new UnableToExecuteActionException(CONST.NOT_CORRECT_TYPE_EXCEPTION);
    }

    var targetCharacterController = game.getCharacterControllerByPosition(specs.target);

    if (targetCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_POSITION_EXCEPTION);
    }

    if (game.isTargetInRange(targetCharacterController.getPosition(), game.getEyeOfTheStorm())) {
      throw new UnableToExecuteActionException(CONST.CHARACTER_IN_SANDSTORM);
    }

    if (!game.isTargetInRange(attackerCharacterController.getPosition(), specs.target)) {
      throw new OutOfRangeException();
    }

    if (targetCharacterController.getCharacterType() != CharacterType.NOBLE) {
      throw new UnableToExecuteActionException(CONST.NOT_CORRECT_TYPE_EXCEPTION);
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID, Action.KANLY,
        specs);

    attackerCharacterController.decreaseAP(attackerCharacterController.getAP());
    targetCharacterController.kanly();

    if (targetCharacterController.isDead()) {
      game.getCurrentPlayer()
          .setEnemiesKilledCounter(game.getCurrentPlayer().getEnemiesKilledCounter() + 1);
    }

    //add character stat change of attacker
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        attackerCharacterController.getStats());

    //add character stat change demand of target
    game.sendCharacterStatChangeDemandMessage(game.getOppositePlayer().getClientID(),
        targetCharacterController.getCharacterID(), targetCharacterController.getStats());

    CONST.logger.info("Successfully executed kanly action!");
  }

  void checkAndDoFamilyAtomicsAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException, NullPointerException {
    var attackerCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);
    if (game.getCurrentPlayer().getAtomicsCounter() <= 0) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_ATOM_BOMBS);
    }
    if (attackerCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }
    if (attackerCharacterController.cannotDoSpecialAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_SPECIAL_EXCEPTION);
    }
    MessageHandler.getInstance().addMessageToQueue(
        new ActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
            Action.FAMILY_ATOMICS, specs));
    game.getCurrentPlayer().useAtomics();
    var wormIsDead = false;
    List<CharacterController> hitCharacters = new ArrayList<>();

    List<GameTile> gameTiles = game.getGameBoard()
        .getTilesInRadiusWithMinDistance(1, specs.target, 0);
    gameTiles.add(game.getGameBoard().getTile(specs.target));

    for (GameTile tile : gameTiles
    ) {
      tile.removeSpice();

      if (tile.isOccupied()) {
        hitCharacters.add(game.getCharacterControllerByPosition(tile.getPosition()));
      }

      if (tile.getTileType() == GameTileType.MOUNTAINS) {
        tile.changeType(GameTileType.PLATEAU);
      } else if (tile.getTileType() == GameTileType.DUNE) {
        tile.changeType(GameTileType.FLAT_SAND);
      }

      if (tile.getTileType() == GameTileType.DUNE_WORM ||
          tile.getTileType() == GameTileType.FLAT_SAND_WORM) {
        wormIsDead = true;
        tile.changeType(GameTileType.FLAT_SAND);
      }
    }

    game.sendMapChangeDemandMessage(ChangeReason.FAMILY_ATOMICS);

    checkHitCharacters(hitCharacters);
    if (wormIsDead) {
      game.getWormController().despawnWorm();
    }
  }

  void checkHitCharacters(List<CharacterController> hitCharacters) {
    if (!hitCharacters.isEmpty() && !game.isAtomicHit()) {
      game.setAtomicHit(true);
      game.getCurrentPlayer().setShunned();
    }
    MessageHandler.getInstance().addMessageToQueue(new AtomicsUpdateDemandMessage(
        game.getCurrentPlayer().getClientID(), game.getCurrentPlayer().isShunned(),
        game.getCurrentPlayer().getAtomicsCounter()));
    for (CharacterController hitCharacter : hitCharacters
    ) {
      hitCharacter.setIsDead(true);
      var player = game.getPlayerByCharacterController(hitCharacter);

      if (player == null) {
        CONST.logger.severe("Character is not assigned to a player!");
        return;
      }

      MessageHandler.getInstance().addMessageToQueue(new CharacterStatChangeDemandMessage(
          player.getClientID(),
          hitCharacter.getCharacterID(), hitCharacter.getStats()));
    }
  }

  void checkAndDoSpiceHoardingAction(int characterID,
      ActionSpecs specs) throws UnableToExecuteActionException {
    var characterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (characterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }

    if (characterController.cannotDoSpecialAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_SPECIAL_EXCEPTION);
    }

    if (characterController.getCharacterType() != CharacterType.MENTAT) {
      throw new UnableToExecuteActionException(CONST.NOT_CORRECT_TYPE_EXCEPTION);
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        Action.SPICE_HOARDING,
        specs);

    List<GameTile> tiles = game.getGameBoard().getTilesInRadiusWithMinDistance(1,
        characterController.getPosition(), 0);
    tiles.add(game.getGameBoard().getTile(characterController.getPosition()));

    var spiceCounter = 0;
    int freeInventorySpace = characterController.getAvailableInventorySpace();
    for (GameTile tile : tiles) {
      if (spiceCounter >= freeInventorySpace) {
        break;
      }

      if (tile.isSpice()) {
        tile.removeSpice();
        spiceCounter++;
      }
    }

    //add map change demand message
    if (spiceCounter >= 1) {
      game.sendMapChangeDemandMessage(ChangeReason.SPICE_PICKUP);
    }

    //add spice to inventory + increase spice counter pickup
    characterController.setSpice(characterController.getSpice() + spiceCounter);
    game.getCurrentPlayer().setSpicePickupCounter(
        game.getCurrentPlayer().getSpicePickupCounter() + spiceCounter);

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        characterController.getStats());

    CONST.logger.info("Successfully executed spice hoarding action!");
  }

  void checkAndDoVoiceAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException, OutOfRangeException {
    var sourceCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (sourceCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }

    if (sourceCharacterController.cannotDoSpecialAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_SPECIAL_EXCEPTION);
    }
    if (sourceCharacterController.getCharacterType() != CharacterType.BENE_GESSERIT) {
      throw new UnableToExecuteActionException(CONST.NOT_CORRECT_TYPE_EXCEPTION);
    }

    var targetCharacterController = game.getCharacterControllerByPosition(specs.target);

    if (targetCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_POSITION_EXCEPTION);
    }

    if (game.isTargetInRange(game.getCurrentCharacter().getPosition(), game.getEyeOfTheStorm())) {
      throw new UnableToExecuteActionException(
          "Cannot execute Action, character is in sandstorm");
    }

    if (!game.isTargetInRange(sourceCharacterController.getPosition(), specs.target)) {
      throw new OutOfRangeException();
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID, Action.VOICE,
        specs);

    int freeInventorySpace = sourceCharacterController.getAvailableInventorySpace();

    if (freeInventorySpace >= targetCharacterController.getSpice()) {
      int stolenSpice = targetCharacterController.getSpice();
      targetCharacterController.setSpice(0);
      sourceCharacterController.setSpice(sourceCharacterController.getSpice() + stolenSpice);
    } else {
      targetCharacterController.setSpice(
          targetCharacterController.getSpice() - freeInventorySpace);
      sourceCharacterController.setSpice(
          sourceCharacterController.getSpice() + freeInventorySpace);
    }

    //decrease ap to 0
    sourceCharacterController.decreaseAP(sourceCharacterController.getAP());

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        sourceCharacterController.getStats());

    var targetPlayer = game.getPlayerByCharacterController(targetCharacterController);
    if (targetPlayer == null) {
      throw new NullPointerException("Found no player for given character controller!");
    }

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(targetPlayer.getClientID(),
        targetCharacterController.getCharacterID(), targetCharacterController.getStats());

    CONST.logger.info("Successfully executed voice action!");
  }

  void checkAndDoSwordSpinAction(int characterID, ActionSpecs specs)
      throws UnableToExecuteActionException {
    var attackerCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);

    if (attackerCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }
    if (attackerCharacterController.cannotDoSpecialAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_SPECIAL_EXCEPTION);
    }
    if (attackerCharacterController.getCharacterType() != FIGHTER) {
      throw new UnableToExecuteActionException(CONST.NOT_CORRECT_TYPE_EXCEPTION);
    }

    //add action demand message
    game.sendActionDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        Action.SWORDSPIN,
        new ActionSpecs(null));

    List<GameTile> tiles = game.getGameBoard().getTilesInRadiusWithMinDistance(1,
        attackerCharacterController.getPosition(), 0);

    //reduce ap to 0
    attackerCharacterController.decreaseAP(attackerCharacterController.getAP());

    //add character stat change of attacker
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        attackerCharacterController.getStats());

    for (GameTile tile : tiles) {
      var targetCharacterController = game.getCharacterControllerByPosition(
          tile.getPosition());

      if (targetCharacterController == null || game.isTargetInRange(
          targetCharacterController.getPosition(), game.getEyeOfTheStorm())) {
        continue;
      }

      if (Objects.equals(game.getPlayerByCharacterController(targetCharacterController),
          game.getOppositePlayer())) {
        attackerCharacterController.attack(targetCharacterController, specs.target);

        if (targetCharacterController.isDead()) {
          game.getCurrentPlayer().setEnemiesKilledCounter(
              game.getCurrentPlayer().getEnemiesKilledCounter() + 1);
        }

        //add character stat change demand of target
        game.sendCharacterStatChangeDemandMessage(game.getOppositePlayer().getClientID(),
            targetCharacterController.getCharacterID(), targetCharacterController.getStats());
      }
    }

    CONST.logger.info("Successfully executed voice action!");
  }

  public void checkTransfer(int sourceCharacterID, int targetCharacterID, int amount)
      throws UnableToExecuteActionException, InconsistentDataException, OutOfRangeException {
    var sourceCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), sourceCharacterID);

    if (sourceCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }
    if (sourceCharacterController.cannotDoNormalAction()) {
      throw new UnableToExecuteActionException(CONST.NOT_ENOUGH_AP_NORMAL_EXCEPTION);
    }
    if (sourceCharacterController.getSpice() < amount) {
      throw new InconsistentDataException("Source character has not enough spice to transfer!");
    }

    var targetCharacterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), targetCharacterID);

    if (targetCharacterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }
    if (!game.isTargetInRange(sourceCharacterController.getPosition(),
        targetCharacterController.getPosition())) {
      throw new OutOfRangeException();
    }

    int freeInventorySpace = targetCharacterController.getAvailableInventorySpace();

    if (freeInventorySpace < amount) {
      throw new InconsistentDataException("Target has not enough inventory space!");
    }

    //add transfer demand message
    game.sendTransferDemandMessage(game.getCurrentPlayer().getClientID(), sourceCharacterID,
        targetCharacterID);

    sourceCharacterController.setSpice(sourceCharacterController.getSpice() - amount);
    targetCharacterController.setSpice(targetCharacterController.getSpice() + amount);

    //decrease ap
    sourceCharacterController.decreaseAP(1);

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(),
        sourceCharacterID,
        sourceCharacterController.getStats());

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getOppositePlayer().getClientID(),
        targetCharacterID,
        targetCharacterController.getStats());

    CONST.logger.info("Successfully executed spice transfer!");
  }
}
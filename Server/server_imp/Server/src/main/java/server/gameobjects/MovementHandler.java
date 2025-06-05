package server.gameobjects;

import exceptions.InconsistentDataException;
import java.util.ArrayList;
import java.util.List;
import messages.Message;
import messages.MovementDemandMessage;
import messages.data.dictionary.MovementSpecs;
import server.MessageHandler;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.GameTileType;
import server.gameobjects.gameboard.character.CharacterController;
import shared_data.CONST;
import shared_data.Position;

public class MovementHandler {

  private final Game game;

  public MovementHandler(Game game) {
    this.game = game;
  }

  public void checkMovement(int characterID, MovementSpecs specs) throws
      InconsistentDataException {
    var characterController = game.getCharacterControllerByPlayerAndCharacterID(
        game.getCurrentPlayer(), characterID);
    GameTile[][] map = game.getGameBoard().getMap();
    var positionForCheck = game.getCurrentCharacter().getPosition();


    if (characterController == null) {
      throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
    }
    if (characterController.isDead()) {
      throw new InconsistentDataException("Character is dead!");
    }


    if (specs.path.size() > characterController.getMovementPoints()) {
      throw new InconsistentDataException("To few movement points!");
    }
    for (Position position : specs.path) {
      if (!map[position.y][position.x].getAccessibility()) {
        throw new InconsistentDataException("Cannot access specific tile!");
      }
      if (!game.isTargetInRange(positionForCheck, position)) {
        throw new InconsistentDataException("Target tile is not in range!");
      }
      if (position.equals(positionForCheck)) {
        throw new InconsistentDataException("Given position is your current position!");
      }
      positionForCheck = position;
    }

    var messages = moveCharacter(characterID, specs);
    for (Message message : messages) {
      MessageHandler.getInstance().addMessageToQueue(message);
    }

    //add movement demand message
    MessageHandler.getInstance().addMessageToQueue(
        new MovementDemandMessage(game.getCurrentPlayer().getClientID(), characterID, specs));

    //add character stat change demand message
    game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
        characterController.getStats());

    game.checkIfTurnEnd(characterID);

    CONST.logger.info("Successfully executed movement!123");
  }

  void checkCitySpiceTransfer(int characterID, CharacterController characterController) {
    if (game.checkPlayerCitySpiceTransfer(characterID) && characterController.getSpice() >= 1) {
      int spiceAmount = characterController.getSpice();
      characterController.setSpice(0);
      game.getCurrentPlayer().setSpice(game.getCurrentPlayer().getSpice() + spiceAmount);
      game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
          characterController.getStats());
      game.sendChangePlayerSpiceDemand(game.getCurrentPlayer().getClientID(), game.getCurrentPlayer().getSpice() + spiceAmount);
    }
  }

  List<MovementDemandMessage> moveCharacter(int characterID, MovementSpecs specs) {
    GameTile[][] map = game.getGameBoard().getMap();
    List<MovementDemandMessage> messages = new ArrayList<>();

    for (Position position : specs.path) {
      if (!map[position.y][position.x].isOccupied()) {
        game.getCurrentCharacter()
            .move(position, game.getGameBoard().getTile(position).getTileType());
      } else {
        var pushed = game.getCharacterControllerByPosition(position);
        if (pushed == null) {
          CONST.logger.severe(CONST.CHARACTER_POSITION_EXCEPTION);
          continue;
        }
        swapPositions(game.getCurrentCharacter(), pushed);
        var pushedPlayer = game.getPlayerByCharacterController(pushed);

        if (pushedPlayer == null) {
          throw new NullPointerException("Found no player for character controller!");
        }

        messages.add(
            new MovementDemandMessage(pushedPlayer.getClientID(), pushed.getCharacterID(),
                new MovementSpecs(
                    new ArrayList<>(List.of(new Position(pushed.getPosition()))))));
      }
      checkCitySpiceTransfer(characterID, game.getCurrentCharacter());
    }
    return messages;
  }

  private void swapPositions(CharacterController pusher, CharacterController pushed) {
    var tmpPos = new Position(pusher.getPosition());
    pusher.move(pushed.getPosition(),
        game.getGameBoard().getTile(pusher.getPosition()).getTileType());
    pushed.moved(tmpPos);
  }

}
package server.gameobjects;

import exceptions.InvalidHouseSelectionException;
import exceptions.InvalidPlayerNumberException;
import java.util.ArrayList;
import java.util.List;
import messages.HouseAcknowledgementMessage;
import messages.SpawnCharacterDemandMessage;
import messages.data.character.MessageCharacter;
import server.MessageHandler;
import server.gameobjects.gameboard.character.CharacterController;
import shared_data.CONST;
import shared_data.Position;
import shared_data.house.House;
import shared_data.house.HouseCharacterMap;
import shared_data.house.HouseName;

public class HouseRequestHandler {

  private final Game game;

  public HouseRequestHandler(Game game) {
    this.game = game;
  }

  public void checkHouseRequest(HouseName houseName, Player player)
      throws InvalidPlayerNumberException, InvalidHouseSelectionException {
    List<House> houseSelection;
    if (player.getPlayerNumber() == 0) {
      houseSelection = game.getHouseSelectionPlayer0();
    } else if (player.getPlayerNumber() == 1) {
      houseSelection = game.getHouseSelectionPlayer1();
    } else {
      throw new InvalidPlayerNumberException();
    }

    for (House house : houseSelection) {
      if (house.houseName == houseName) {
        MessageHandler.getInstance().addMessageToQueue(
            new HouseAcknowledgementMessage(player.getClientID(), houseName));
        CONST.logger.info("Successfully selected house!");

        assignHouseNameToPlayer(houseName, player);
        constructAndSpawnCharactersForPlayer(player);

        return;
      }
    }

    throw new InvalidHouseSelectionException();
  }

  public void assignHouseNameToPlayer(HouseName houseName, Player player) {
    player.setHouseName(houseName);
  }

  public void constructAndSpawnCharactersForPlayer(Player player) {
    List<MessageCharacter> messageCharacters = HouseCharacterMap.getInstance()
        .getCharactersForHouse(player.getHouseName());
    List<Position> gameCharacterSpawnPositions = game.generateSpawningPositions(player);

    List<CharacterController> characterControllers = new ArrayList<>();
    CharacterController tmpController;
    Position tmpPosition;

    for (MessageCharacter messageCharacter : messageCharacters) {
      tmpPosition = gameCharacterSpawnPositions.get(0);
      tmpController = game.constructCharacterController(messageCharacter, tmpPosition);
      characterControllers.add(tmpController);

      if (tmpController == null) {
        throw new NullPointerException(CONST.CHARACTER_POSITION_EXCEPTION);
      }

      MessageHandler.getInstance().addMessageToQueue(
          new SpawnCharacterDemandMessage(player.getClientID(), tmpController.getCharacterID(), tmpController.getCharacterName(),
              tmpPosition, tmpController.getAttributes()));

      gameCharacterSpawnPositions.remove(0);
    }

    player.setCharacterControllers(characterControllers);

    CONST.logger.info("Successfully constructed characters for player!");
  }

  public void assignCityToPlayer() {
    List<Position> cityPositions = game.getGameBoard().findCityPositions();
    for (var i = 0; i < 2; i++) {
      game.getPlayers()[i].setCityPosition(cityPositions.get(i));
      game.getGameBoard()
          .assignCityTileToClientID(cityPositions.get(i), game.getPlayers()[i].getClientID());
    }
  }
}
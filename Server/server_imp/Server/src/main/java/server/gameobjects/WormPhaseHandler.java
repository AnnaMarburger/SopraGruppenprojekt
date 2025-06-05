package server.gameobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import messages.CharacterStatChangeDemandMessage;
import messages.SandwormDespawnDemandMessage;
import messages.SandwormSpawnDemandMessage;
import server.MessageHandler;
import server.gameobjects.gameboard.GameTile;
import server.gameobjects.gameboard.character.CharacterController;
import server.gameobjects.gameboard.character.GameCharacter;
import shared_data.CONST;
import shared_data.Position;

public class WormPhaseHandler {

  private final PhaseHandler phaseHandler;

  public WormPhaseHandler(PhaseHandler phaseHandler) {
    this.phaseHandler = phaseHandler;
  }//region Sandworm Phase

  void startSandwormPhase() {
    wormPhase();
    phaseHandler.getGame().setGameState(GameState.CLONE);
    CONST.logger.info("Successfully executed sandworm phase");
  }

  void startShaiHuludPhase() {
    shaiHuludPhase();
    phaseHandler.getGame().setGameState(GameState.CLONE);
    CONST.logger.info("Successfully executed shai hulud phase");
  }

  void wormPhase() {
    if (!phaseHandler.getGame().getWormController().isActive()) {
      HashMap<CharacterController, Integer> characters = getLoudCharacters();
      CharacterController character = getRandomCharacter(
          new LinkedList<>(characters.keySet()));
      if (character == null)  // When no Character was found
      {
        return;
      }
      spawnWorm(character.getGameCharacter(), characters.get(character));
    } else {
      huntPhaseWorm();
    }
  }

  void huntPhaseWorm() {
    List<Position> positions = phaseHandler.getGame().getWormController().hunt();
    if (positions == null) {
      return;
    }
    if (positions.isEmpty()) {
      respawnWorm();
      return;
    }
    if (phaseHandler.getGame().getWormController().canEat()) {
      wormEat();
      checkRegularEnd();
    }
  }

  void checkRegularEnd() {
    for (Player player : phaseHandler.getGame().getPlayers()) {
      if (player.getCloneableCharacters().isEmpty()) {
        MessageHandler.getInstance()
            .sendGameEnd(phaseHandler.getGame().getPlayers()[player.getPlayerNumber() == 0 ? 1
                    : 0].getClientID(),
                player.getClientID());
        phaseHandler.getGame().setGameState(GameState.GAME_END);
        break;
      }
    }
  }

  void wormEat() {
    CONST.logger.info("WORM EATING CHARACTER");
    var eatenCharacter = phaseHandler.getGame().getCharacterControllerByPosition(
        phaseHandler.getGame().getWormController().getTarget().getPosition());
    if (eatenCharacter == null) {
      CONST.logger.severe("CHARACTER TO EAT WAS NOT FOUND!");
      return;
    }
    eatenCharacter.decreaseHP(eatenCharacter.getHP());
    eatenCharacter.setIsDead(true);
    eatenCharacter.setCloneable(false);
    MessageHandler.getInstance().addMessageToQueue(new CharacterStatChangeDemandMessage(
            Game.getGameInstance().getPlayerByCharacterController(eatenCharacter).getClientID(),eatenCharacter.getCharacterID(),eatenCharacter.getStats()
    ));
    if(eatenCharacter.getGameCharacter().equals(
            phaseHandler.getGame().getWormController().getTarget())
    )
      phaseHandler.getGame().getWormController().despawnWorm();
    var player = phaseHandler.getGame().getPlayerByCharacterController(eatenCharacter);
    if (player == null) {
      CONST.logger.severe(
          "COULDNT FIND PLAYER WITH CHARACTER ID: " + eatenCharacter.getCharacterID());
      return;
    }

    player.setCharactersEatenCounter(player.getCharactersEatenCounter() + 1);

    MessageHandler.getInstance()
        .addMessageToQueue(new CharacterStatChangeDemandMessage(player.getClientID(),
            eatenCharacter.getCharacterID(),
            eatenCharacter.getStats()));
    phaseHandler.getGame().getWormController().despawnWorm();
    
  }

  void respawnWorm() {
    var player = phaseHandler.getGame().getPlayerByCharacterController(
        phaseHandler.getGame().getCharacterControllerByPosition(
            phaseHandler.getGame().getWormController().getTarget().getPosition()));
    if (player == null) {
      CONST.logger.severe("TARGETED PLAYER FOR WORM WAS NOT FOUND!");
      return;
    }

    spawnWorm(phaseHandler.getGame().getWormController().getTarget(), player.getClientID());
  }

  void spawnWorm(GameCharacter character, int clientID) {
    List<GameTile> tileList = phaseHandler.getGame().getGameBoard().getTilesInRadiusWithMinDistance(
        phaseHandler.getGame().getWormController().getWormSpawnRadius(),
        character.getPosition(), 1);
    tileList.remove(Game.getGameInstance().getGameBoard().getTile(character.getPosition()));

    Optional<GameTile> tile = phaseHandler.getGame().getGameBoard().getRandomTile(tileList,
        phaseHandler.getGame().getGameBoard()::isValidSpawnForWorm);

    tile.ifPresentOrElse(x -> {
      phaseHandler.getGame().getWormController().startHunt(character, clientID, x.getPosition());

    }, () -> { //Worm can't spawn
    });
  }

  HashMap<CharacterController, Integer> getLoudCharacters() {
    HashMap<CharacterController, Integer> characterList = new HashMap<>();
    for (Player player : phaseHandler.getGame().getPlayers()) {
      for (CharacterController controller : player.getCharacterControllers()) {
        if (!controller.isDead() && controller.isSound()) {
          characterList.put(controller, player.getClientID());
        }
      }
    }
    return characterList;
  }

  void shaiHuludPhase() {
    if (phaseHandler.getGame().getWormController().isActive()) {
      phaseHandler.getGame().getWormController().despawnWorm();
    }
    HashMap<CharacterController, Integer> characters = phaseHandler.getGame().getAliveCharacters();
    CharacterController character = getRandomCharacter(
        new LinkedList<>(characters.keySet()));
    if (character == null) {
      checkOvertimeEnd(phaseHandler.getGame().getCurrentPlayer().getCharacterControllers().get(0));
      return;
    }
    shaiHuludHunt(character, characters.get(character));
    checkOvertimeEnd(character);
  }

  void shaiHuludHunt(CharacterController character, int clientID) {
    MessageHandler.getInstance().addMessageToQueue(new SandwormSpawnDemandMessage(clientID,
        character.getCharacterID(), character.getPosition()));
    character.setIsDead(true);
    character.setCloneable(false);
    MessageHandler.getInstance().addMessageToQueue(
        new CharacterStatChangeDemandMessage(clientID, character.getCharacterID(),
            character.getStats()));
    //MessageHandler.getInstance().addMessageToQueue(new SandwormDespawnDemandMessage());
  }

  void checkOvertimeEnd(CharacterController lastEaten) {
    if (!checkIfCharactersOnMap()) {
      if (compareSpiceAmount())
        //Shall be empty!
        ;
      else if (compareSpicePickup())
        //Shall be empty!
        ;
      else if (compareCharactersKilled())
        //Shall be empty!
        ;
      else if (compareCharactersEaten())
        //Shall be empty!
        ;
      else {
        var player = phaseHandler.getGame().getPlayerByCharacterController(lastEaten);

        if (player == null) {
          CONST.logger.severe("Character controller is not assigned to player!");
          return;
        }

        int winner = player.getPlayerNumber();
        MessageHandler.getInstance()
            .sendGameEnd(phaseHandler.getGame().getPlayers()[winner].getClientID(),
                phaseHandler.getGame().getPlayers()[winner == 0 ? 1 : 0].getClientID());
      }
      phaseHandler.getGame().setGameState(GameState.GAME_END);
    }
  }

  boolean compareSpiceAmount() {
    if (phaseHandler.getGame().getPlayers()[0].getSpice() > phaseHandler.getGame()
        .getPlayers()[1].getSpice()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[0].getClientID(),
              phaseHandler.getGame().getPlayers()[1].getClientID());
      return true;
    } else if (phaseHandler.getGame().getPlayers()[0].getSpice() < phaseHandler.getGame()
        .getPlayers()[1].getSpice()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[1].getClientID(),
              phaseHandler.getGame().getPlayers()[0].getClientID());
      return true;
    }
    return false;
  }

  boolean compareSpicePickup() {
    if (phaseHandler.getGame().getPlayers()[0].getSpicePickupCounter()
        > phaseHandler.getGame().getPlayers()[1].getSpicePickupCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[0].getClientID(),
              phaseHandler.getGame().getPlayers()[1].getClientID());
      return true;
    } else if (phaseHandler.getGame().getPlayers()[0].getSpicePickupCounter()
        < phaseHandler.getGame().getPlayers()[1].getSpicePickupCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[1].getClientID(),
              phaseHandler.getGame().getPlayers()[0].getClientID());
      return true;
    }
    return false;
  }

  boolean compareCharactersKilled() {
    if (phaseHandler.getGame().getPlayers()[0].getEnemiesKilledCounter()
        > phaseHandler.getGame().getPlayers()[1].getEnemiesKilledCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[0].getClientID(),
              phaseHandler.getGame().getPlayers()[1].getClientID());
      return true;
    } else if (phaseHandler.getGame().getPlayers()[0].getEnemiesKilledCounter()
        < phaseHandler.getGame().getPlayers()[1].getEnemiesKilledCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[1].getClientID(),
              phaseHandler.getGame().getPlayers()[0].getClientID());
      return true;
    }
    return false;
  }

  boolean compareCharactersEaten() {
    if (phaseHandler.getGame().getPlayers()[0].getCharactersEatenCounter()
        < phaseHandler.getGame().getPlayers()[1].getCharactersEatenCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[0].getClientID(),
              phaseHandler.getGame().getPlayers()[1].getClientID());
      return true;
    } else if (phaseHandler.getGame().getPlayers()[0].getCharactersEatenCounter()
        > phaseHandler.getGame().getPlayers()[1].getCharactersEatenCounter()) {
      MessageHandler.getInstance()
          .sendGameEnd(phaseHandler.getGame().getPlayers()[1].getClientID(),
              phaseHandler.getGame().getPlayers()[0].getClientID());
      return true;
    }
    return false;
  }

  boolean checkIfCharactersOnMap() {
    for (Player player : phaseHandler.getGame().getPlayers()) {
      if (player.checkIfCharactersOnMap()) {
        return true;
      }
    }
    return false;
  }

  CharacterController getRandomCharacter(List<CharacterController> characters) {
    Collections.shuffle(characters);
    if (characters.iterator().hasNext()) {
      return characters.iterator().next();
    }
    return null;
  }
}
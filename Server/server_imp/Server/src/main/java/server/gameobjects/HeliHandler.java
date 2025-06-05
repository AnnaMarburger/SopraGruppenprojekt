package server.gameobjects;

        import exceptions.InconsistentDataException;
        import java.util.ArrayList;
        import java.util.List;

        import messages.HeliDemandMessage;
        import messages.Message;
        import messages.MovementDemandMessage;
        import messages.data.dictionary.MovementSpecs;
        import server.MessageHandler;
        import server.gameobjects.gameboard.GameTile;
        import server.gameobjects.gameboard.GameTileType;
        import server.gameobjects.gameboard.character.CharacterController;
        import shared_data.CONST;
        import shared_data.Position;
        import shared_data.config.PartyConfig;

public class HeliHandler {

    private final Game game;

    public HeliHandler(Game game) {
        this.game = game;
    }

    public void checkHeli(int characterID, Position target) throws
            InconsistentDataException {

        CONST.logger.info("starting checkHeli.");
        var characterController = game.getCharacterControllerByPlayerAndCharacterID(
                game.getCurrentPlayer(), characterID);
        GameTile[][] map = game.getGameBoard().getMap();
        var positionForCheck = game.getCurrentCharacter().getPosition();

        //Heliport Vars
        var positionDestination = target; //Destination Position Tile
        GameTile tileStart = map[target.y][target.x];
        GameTile tileDest = map[positionDestination.y][positionDestination.x];

        CONST.logger.info("check if heliport usage is allowed");
        if (characterController == null) {
            throw new NullPointerException(CONST.CHARACTER_ID_EXCEPTION);
        }
        if (characterController.isDead()) {
            throw new InconsistentDataException("Character is dead!");
        }
        if (characterController.getMovementPoints() < 1) {
            throw new InconsistentDataException("To few movement points!");
        }
        if (tileDest.getTileType().equals(GameTileType.HELIPORT)){
            CONST.logger.info("-----Check: Dest Field Heliport");
        }
        if (tileStart.getTileType().equals(GameTileType.HELIPORT)){
            CONST.logger.info("-----Check: Start Field Heliport");
        }
        if (characterController.getMovementPoints() >= 1){
            CONST.logger.info("-----Check: Enough Movement Points");
        }
        CONST.logger.info("usage allowed");

        //HeliportMovement tileDest.getTileType() == GameTileType.HELIPORT && tileStart.getTileType() == GameTileType.HELIPORT && characterController.getMovementPoints() >= 1
        //tileDest.getTileType() == GameTileType.HELIPORT && tileStart.getTileType() == GameTileType.HELIPORT && characterController.getMovementPoints() >= 1
        if (tileDest.getTileType().equals(GameTileType.HELIPORT) && tileStart.getTileType().equals(GameTileType.HELIPORT) && characterController.getMovementPoints() >= 1){
            CONST.logger.info("Heliport used");
            if (checkIfHeliCrashes(positionForCheck, positionDestination)){
                //place player on random tile
                CONST.logger.info("execute heli crash.");
                game.getCurrentCharacter().decreaseMP(1);
                Position positionRandom = game.getGameBoard().randomAccessiblePosition();
                game.getCurrentCharacter().moved(positionRandom);

                //send heliport demand message with crash info
                MessageHandler.getInstance().addMessageToQueue(
                        new HeliDemandMessage(game.getCurrentPlayer().getClientID(), characterID, positionRandom, true));

                //spreading the spice around the player
                game.getGameBoard().spiceSpread(game.getCurrentCharacter().getSpice(), positionRandom);
                game.getCurrentCharacter().setSpice(0);

                //add character stat change demand message
                game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
                        characterController.getStats());

                game.checkIfTurnEnd(characterID);

                CONST.logger.info("Successfully executed Heli crash!");
            }else {
                //move player to destination heliport
                game.getCurrentCharacter().decreaseMP(1);
                game.getCurrentCharacter().moved(positionDestination);

                //send heliport demand message
                //List<Position> poslist = new ArrayList<>();
                //poslist.add(positionDestination);
                //MovementSpecs specs = new MovementSpecs(poslist);
                //MessageHandler.getInstance().addMessageToQueue(
                        //new MovementDemandMessage(game.getCurrentPlayer().getClientID(), characterID, specs));
                MessageHandler.getInstance().addMessageToQueue(
                        new HeliDemandMessage(game.getCurrentPlayer().getClientID(), characterID, positionDestination, false));

                //add character stat change demand message
                game.sendCharacterStatChangeDemandMessage(game.getCurrentPlayer().getClientID(), characterID,
                        characterController.getStats());

                game.checkIfTurnEnd(characterID);

                CONST.logger.info("Successfully executed Heli flight!");
            }
        }
        else{
            CONST.logger.info("not enough move points or start and dest are not type heliport");
        }



        //move character
        /*
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
        } */

        /*var messages = moveCharacter(characterID, positionDestination);
        for (Message message : messages) {
            MessageHandler.getInstance().addMessageToQueue(message);
        } */

        //add movement demand message

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

    List<MovementDemandMessage> moveCharacter(int characterID, Position destination) {
        GameTile[][] map = game.getGameBoard().getMap();
        List<MovementDemandMessage> messages = new ArrayList<>();

            if (!map[destination.y][destination.x].isOccupied()) {
                game.getCurrentCharacter()
                        .move(destination, game.getGameBoard().getTile(destination).getTileType());
            } else {
                var pushed = game.getCharacterControllerByPosition(destination);
                if (pushed == null) {
                    CONST.logger.severe(CONST.CHARACTER_POSITION_EXCEPTION);
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

        return messages;
    }

    private void swapPositions(CharacterController pusher, CharacterController pushed) {
        var tmpPos = new Position(pusher.getPosition());
        pusher.move(pushed.getPosition(),
                game.getGameBoard().getTile(pusher.getPosition()).getTileType());
        pushed.moved(tmpPos);
    }

    public boolean checkIfHeliCrashes(Position start, Position dest){
        CONST.logger.info("checking Crash");
        if (checkSandstorm(start, dest)){
            CONST.logger.info("hitting sandstorm");
            if (Math.random() > PartyConfig.getCurrentConfig().crashProbability){
                return false;

            }
            else{
                return true;
            }
        }
        else{
            CONST.logger.info("not hitting sandstorm");
            return false;
        }
    }

    public boolean checkSandstorm(Position start, Position dest){
        CONST.logger.info("checking if moving through storm");
        Position eyeOfTheStorm = game.getEyeOfTheStorm();

        if (start.y == dest.y){
            if (inRange(eyeOfTheStorm.y, start.y-1, dest.y+1) && inRange(eyeOfTheStorm.x, start.x, dest.x)){
                return true;
            } else {
                return false;
            }
        }

        float a = (start.x - dest.x)/(start.y - dest.y);
        float b = start.x - (a * start.y);

        if (inRange(calcY(a,b,eyeOfTheStorm.y +1), eyeOfTheStorm.x+1, eyeOfTheStorm.x-1) && inRange(calcY(a,b,eyeOfTheStorm.y-1), start.x , dest.x)){
            return  true;
        } else if (inRange(calcY(a,b,eyeOfTheStorm.y -1), eyeOfTheStorm.x+1, eyeOfTheStorm.x-1) && inRange(calcY(a,b,eyeOfTheStorm.y+1), start.x , dest.x)){
            return true;
        } else if (inRange(calcX(a,b,eyeOfTheStorm.x -1), eyeOfTheStorm.y+1, eyeOfTheStorm.y-1) && inRange(calcX(a,b,eyeOfTheStorm.x-1), start.y , dest.y)){
            return true;
        } else if (inRange(calcX(a,b,eyeOfTheStorm.x +1), eyeOfTheStorm.y+1, eyeOfTheStorm.y-1) && inRange(calcX(a,b,eyeOfTheStorm.x+1), start.y , dest.y)){
            return true;
        } else {
            return false;
        }
    }

    public float calcX(float a, float b, int x){
        return  (a*x)+b;
    }

    public float calcY(float a, float b, int y){
        return (y-b)/a;
    }

    public boolean inRange(float a, int upper, int lower){
        if (upper < lower){
            int temp = lower;
            lower = upper;
            upper = temp;
        }
        if (upper >= a && a >= lower){
            return true;
        }
        else {
            return false;
        }
    }

}
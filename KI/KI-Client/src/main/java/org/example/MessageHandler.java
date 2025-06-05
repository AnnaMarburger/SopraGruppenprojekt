package org.example;

import com.google.gson.Gson;
import messages.*;
import messages.data.MessageType;
import messages.data.tile.MessageTile;
import org.java_websocket.client.WebSocketClient;
import shared_data.CityToClient;
import shared_data.house.HouseName;

import java.util.LinkedList;
import java.util.List;

public class MessageHandler {


    static Gson gson;
    private int clientID;
    private List<Character> characterList;
    private List<Spice> spiceList;
    private WebSocketClient webSocketClient;
    private int cityX;
    private int cityY;


    public MessageHandler() {
        gson = new Gson();
        characterList = new LinkedList<Character>();
        spiceList = new LinkedList<Spice>();
    }

    /**
     * gets a message and decides what to do
     * @param json
     * @return answer message as String
     */
    public String parseMessage(String json) {

        MessageType messageType;
        try {
            messageType = findMessageType(json);
            //System.out.println("received: " + messageType);
        } catch (Exception e) {
            messageType = MessageType.FAIL;
        }

        //Message message;
        switch (messageType) {
            case JOINACCEPTED:
                return joinAccepted(json);
            case HOUSE_OFFER:
                return houseOffer(json);
            case SPAWN_CHARACTER_DEMAND:
                return spawnCharacter(json);
            case TURN_DEMAND:
                return turnDemand(json);
            case MAP_CHANGE_DEMAND:
                mapChange(json);
                return "";
            case STRIKE:
                System.out.println(json);
                return "";
            case MOVEMENT_DEMAND:
                movementDemand(json);
                return "";
            case CHARACTER_STAT_CHANGE_DEMAND:
                characterChanged(json);
                return "";
            case GAMECFG:
                gameCFG(json);
                return "";
            case GAME_END:
                gameEnd(json);
                return"";
            default:
                //System.out.println("Message not handled yet: "+messageType);
                //System.out.println(json);
        }
        return "";
    }

    /**
     * handles the gameEnd-message
     * @param json incoming message as String
     */
    private void gameEnd(String json) {
        GameEndMessage gameEndMessage = gson.fromJson(json, GameEndMessage.class);
        if(gameEndMessage.winnerID==clientID){
            System.out.println("KI won! Nice!");
        }
        else{
            System.out.println("KI lost!");
        }
        delay();
        delay();
        delay();
    }
    /**
     * handles the gamecfg-message
     * @param json incoming message as String
     */
    private void gameCFG(String json) {
        GamecfgMessage gamecfgMessage = gson.fromJson(json, GamecfgMessage.class);
        CityToClient[] cityToClient = gamecfgMessage.citiesToClients;
        for(int i=0;i<cityToClient.length;i++){
            if(cityToClient[i].clientID==clientID){
                cityX=cityToClient[i].x;
                cityY=cityToClient[i].y;
                break;
            }
        }
    }

    /**
     * handles the characterchanged-message. updates the characters
     * @param json incoming message as String
     */
    private void characterChanged(String json) {
        CharacterStatChangeDemandMessage characterStatChangeDemandMessage = gson.fromJson(json, CharacterStatChangeDemandMessage.class);
        Character character = findCharacterbyID(characterStatChangeDemandMessage.characterID);

        if (characterStatChangeDemandMessage.stats.HP<=0||characterStatChangeDemandMessage.stats.isSwallowed){
            characterList.remove(character);
            return;
        }

        if(character==null){
            return;
        }

        character.inventory = characterStatChangeDemandMessage.stats.spice;

    }
    /**
     * handles the movement demand-message. updates theposition of the characters
     * @param json incoming message as String
     */
    private void movementDemand(String json) {
        MovementDemandMessage movementDemandMessage = gson.fromJson(json, MovementDemandMessage.class);
        if (movementDemandMessage.clientID != clientID) {
            return;
        }
        Character character = findCharacterbyID(movementDemandMessage.characterID);
        character.xCoord = movementDemandMessage.specs.getLast().x;
        character.yCoord = movementDemandMessage.specs.getLast().y;





    }

    /**
     * lets a acharacter collect an spice by sending the actionrequest message to server
     * @param character
     */
    public void collectSpice(Character character){
        String collectMessage = "{\n\"type\": \"ACTION_REQUEST\",\n\"version\": \"0.1\",\n\"clientID\": "+clientID+",\n\"characterID\": "+character.characterID+",\n\"action\": \"COLLECT\",\n\"specs\": {\"target\": {\"x\": "+character.xCoord+",\"y\": "+character.yCoord+"}}\n}";
        System.out.println(collectMessage);
        webSocketClient.send(collectMessage);
    }

    /**
     * handles the turndemand-message by moving to a spice or a to the city. ending the turn by sending an turn end message
     * @param json incoming message as String
     */
    private String turnDemand(String json) {
        delay();
        TurnDemandMessage turnDemandMessage = gson.fromJson(json, TurnDemandMessage.class);
        if (turnDemandMessage.clientID != clientID) {
            return "";
        }

        Character character = findCharacterbyID(turnDemandMessage.characterID);

        System.out.println("TurnDemand for: (" + character.xCoord + ", " + character.yCoord + " )");

        int characterID = turnDemandMessage.characterID;

        moveCharacter(character);

        return endTurn(character);
    }

    /**
     * handles the mapChange-message. especially gets the locations of spice on the map
     * @param json incoming message as String
     */
    private void mapChange(String json) {
        MapChangeDemandMessage mapChangeDemandMessage = gson.fromJson(json, MapChangeDemandMessage.class);
        MessageTile[][] map = mapChangeDemandMessage.newMap;


        spiceList = new LinkedList<Spice>();


        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j].hasSpice) {
                    spiceList.add(new Spice(j, i));
                }
            }
        }
        System.out.println(spiceList);
    }

    /**
     * looks for an character in characteList by the given character ID
     * @param characterID
     * @return the character object with the wanted id
     */
    private Character findCharacterbyID(int characterID) {
        Character character = null;
        for (int i = 0; i < characterList.size(); i++) {
            Character currentCharacter = characterList.get(i);
            if (currentCharacter.characterID == characterID) {
                character = currentCharacter;
                break;
            }
        }
        return character;
    }

    /**
     * decides if an chaarcter is on his way to a spice or to a city. calls the according method
     * @param character
     */
    private void moveCharacter(Character character) {

        if(character==null){
            return;
        }

        if (character.inventory == 0) {
            moveToSpice(character);
        } else {
            moveToCity(character);
        }
    }

    /**
     * lets a character move to his city
     * @param character
     */
    private void moveToCity(Character character) {
        System.out.println("RETURN TO CITYYYY");
        int characterID = character.characterID;
        int characterX = character.xCoord;
        int characterY = character.yCoord;

        int targetX = cityX;
        System.out.println("CITYX: "+targetX);
        int targetY = cityY;

        System.out.println("CITYY: "+targetY);

        if (character.xCoord != targetX) {
            if (targetX < characterX) {
                sendMoveRequest(characterID, characterX - 1, characterY);
            } else if (targetX > characterX) {
                sendMoveRequest(characterID, characterX + 1, characterY);
            }
        }
        else if(character.xCoord == targetX){
            if (targetY < characterY) {
                sendMoveRequest(characterID, characterX, characterY-1);
            } else if (targetX > characterX) {
                sendMoveRequest(characterID, characterX, characterY+1);
            }
        }
    }

    /**
     * lets a character move to the spice he have chosen
     *
     * @param character
     */
    private void moveToSpice(Character character) {
        if (character.currentWantedSpice == null) {
            System.out.println("no Spice selected yet");
            character.chooseRandomSpice(spiceList);
        }
        if (!spiceStillOnMap(character.currentWantedSpice)) {
            System.out.println("spice not on map anymore");
            character.chooseRandomSpice(spiceList);
        }

        int characterID = character.characterID;
        int characterX = character.xCoord;
        int characterY = character.yCoord;

        int targetX = character.currentWantedSpice.xCoord;
        int targetY = character.currentWantedSpice.yCoord;

        if (character.xCoord != targetX) {
            if (targetX < characterX) {
                sendMoveRequest(characterID, characterX - 1, characterY);
            } else if (targetX > characterX) {
                sendMoveRequest(characterID, characterX + 1, characterY);
            }
        }
        else if(character.xCoord == targetX){
            if (targetY < characterY) {
                sendMoveRequest(characterID, characterX, characterY-1);
            } else if (targetX > characterX) {
                sendMoveRequest(characterID, characterX, characterY+1);
            }
        }

    }

    /**
     * sends moveRequestto the server (step by step only to avoid the sandworm attack)
     * @param characterID
     * @param targetX
     * @param targetY
     */
    public void sendMoveRequest(int characterID, int targetX, int targetY) {
        String message = "{\n\"type\": \"MOVEMENT_REQUEST\",\n\"version\": \"0.1\",\n\"clientID\": " + clientID + ",\n\"characterID\": " + characterID + ",\n\"specs\": {\"path\": [{\"x\": " + targetX + ", \"y\": " + targetY + "}]}\n}";

        System.out.println(message);

        webSocketClient.send(message);
    }

    /**
     * checks if the chosen Spice is still on the map
     * @param currentWantedSpice
     * @return true or false
     */
    private boolean spiceStillOnMap(Spice currentWantedSpice) {
        for(int i=0;i<spiceList.size();i++){
            if(currentWantedSpice.xCoord==spiceList.get(i).xCoord&&currentWantedSpice.yCoord==spiceList.get(i).yCoord){
                return true;
            }
        }
        return false;
    }

    /**
     * ends the turn of a character
     * @param character
     * @return
     */
    private String endTurn(Character character) {

        if(character.xCoord==character.currentWantedSpice.xCoord&&character.yCoord==character.currentWantedSpice.yCoord){
            collectSpice(character);
        }

        EndTurnRequestMessage endTurnRequestMessage = new EndTurnRequestMessage(clientID, character.characterID);

        return endTurnRequestMessage.toString();
    }

    /**
     * creates a character with the given data and saves him in a list
     * @param json
     * @return
     */
    private String spawnCharacter(String json) {
        SpawnCharacterDemandMessage spawnCharacterDemandMessage = gson.fromJson(json, SpawnCharacterDemandMessage.class);
        if (clientID != spawnCharacterDemandMessage.clientID) {
            return "";
        }
        int characterID = spawnCharacterDemandMessage.characterID;
        int xCoord = spawnCharacterDemandMessage.position.x;
        int yCoord = spawnCharacterDemandMessage.position.y;
        int movementPoints = spawnCharacterDemandMessage.attributes.mpMax;

        Character newCharacter = new Character(characterID, xCoord, yCoord, movementPoints);
        characterList.add(newCharacter);
        return "";
    }

    /**
     * saves the KI-ID out of the JoinAccepted Message
     * @param json
     * @return
     */
    private String joinAccepted(String json) {
        JoinAcceptedMessage joinAcceptedMessage = gson.fromJson(json, JoinAcceptedMessage.class);
        clientID = joinAcceptedMessage.clientID;
        System.out.println("KI-ID: " + clientID);
        return "";
    }

    /**
     * Method to slow down the ai.
     */
    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    /**
     * always chooses the first house in house offer list and answers with a house request
     * @param json
     * @return
     */
    private String houseOffer(String json) {

        delay();

        HouseOfferMessage houseOfferMessage = gson.fromJson(json, HouseOfferMessage.class);
        if (houseOfferMessage.clientID != clientID) {
            return "";
        }
        HouseName housename = houseOfferMessage.houses.get(0).houseName;
        HouseRequestMessage houseRequestMessage = new HouseRequestMessage(housename);
        return houseRequestMessage.toString();
    }


    /**
     * This method finds the type of message in the json format.
     *
     * @param json json string
     * @return MessageType of the json string
     */
    public static MessageType findMessageType(String json) {
        TypeHolder typeHolder = gson.fromJson(json, TypeHolder.class);
        return MessageType.valueOf(typeHolder.type);
    }

    public void setWebSocket(WebSocketClient client) {
        this.webSocketClient = client;
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
}
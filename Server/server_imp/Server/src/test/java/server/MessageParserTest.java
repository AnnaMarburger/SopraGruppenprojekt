package server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import messages.*;
import shared_data.Action;
import messages.data.MessageType;
import messages.data.dictionary.ActionSpecs;
import messages.data.dictionary.MovementSpecs;
import shared_data.Position;
import shared_data.house.HouseName;
import server.MessageParser.TypeHolder;
import org.junit.jupiter.api.Test;

class MessageParserTest {

  @Test
  void testFailedMessage() {
    String json1 = """
        {
          "type": "FAIL",
          "a": 1.0,
          "coe": 1234,
          "j": "This is a test."
        }""";
    String json2 = "";

    assertFalse(MessageParser.parseMessage(json1).isPresent());
    assertFalse(MessageParser.parseMessage(json2).isPresent());
  }

  @Test
  void testTypeHolder() {
    Gson gson = new Gson();
    String json = """
        {
          "type": "DEBUG",
          "version": 1.0,
          "code": 1234,
          "explanation": "This is a test."
        }""";

    TypeHolder typeHolder1 = new TypeHolder("DEBUG");
    TypeHolder typeHolder2 = gson.fromJson(json, TypeHolder.class);

    assertEquals(typeHolder1.type, typeHolder2.type);
  }

  @Test
  void testJoinMessage() {
    String json = """
        {
          "type": "JOIN",
          "version": 1.0,
          "clientName": "Test",
          "connectionCode": "connect",
          "isActive": true,
          "isCpu": false
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    JoinMessage message = (JoinMessage) MessageParser.parseMessage(json).get();

    assertSame(MessageType.JOIN, message.type);
    assertEquals("1.0", message.version);
    assertEquals("Test", message.clientName);

    boolean active = true;
    assertEquals(active, message.isActive);

    boolean isCpu = false;
    assertEquals(isCpu, message.isCpu);
  }

  @Test
  void testHouseRequestMessage() {
    String json = """
        {
          "type": "HOUSE_REQUEST",
          "version": 1.0,
          "houseName": "ATREIDES"
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    HouseRequestMessage message = (HouseRequestMessage) MessageParser.parseMessage(json).get();

    assertSame(MessageType.HOUSE_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(HouseName.ATREIDES, message.houseName);
  }

  @Test
  void testMovementRequestMessage() {
    String json = """
        {
          "type": "MOVEMENT_REQUEST",
          "version": 1.0,
          "clientID": 1234,
          "characterID": 12,
          "specs": {
            "path": [
              {"x": 1, "y": 2},
              {"x": 2, "y": 2},
              {"x": 2, "y": 3}
            ]
          }
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    MovementRequestMessage message = (MovementRequestMessage) MessageParser.parseMessage(json)
        .get();

    assertSame(MessageType.MOVEMENT_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
    assertEquals(12, message.characterID);

    List<Position> positions = new ArrayList<>(
        Arrays.asList(new Position(1, 2), new Position(2, 2), new Position(2, 3)));
    MovementSpecs specs = new MovementSpecs(positions);
    assertEquals(specs, message.specs);
  }

  @Test
  void testHeliRequestMessage() {
    String json = """
        {
          "type": "HELI_REQUEST",
          "version": 1.0,
          "clientID": 1234,
          "characterID": 12,
          "target": {"x": 1, "y": 2}
          
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    HeliRequestMessage message = (HeliRequestMessage) MessageParser.parseMessage(json)
            .get();

    assertSame(MessageType.HELI_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
    assertEquals(12, message.characterID);

    Position target = new Position(1,2);
    assertEquals(target, message.target);
  }

  @Test
  void testActionRequestMessage() {
    String json = """
        {
          "type": "ACTION_REQUEST",
          "version": 1.0,
          "clientID": 1234,
          "characterID": 12,
          "action" : "VOICE",
          "specs": {
            "target": {
              "x": 2,
              "y": 3
            }
          }
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    ActionRequestMessage message = (ActionRequestMessage) MessageParser.parseMessage(json).get();

    assertSame(MessageType.ACTION_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
    assertEquals(12, message.characterID);
    assertEquals(Action.VOICE, message.action);

    Position target = new Position(2, 3);
    ActionSpecs specs = new ActionSpecs(target);
    assertEquals(specs, message.specs);
  }

  @Test
  void testEndTurnRequestMessage() {
    String json = """
        {
          "type": "END_TURN_REQUEST",
          "version": 1.0,
          "clientID": 1234,
          "characterID": 12
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    EndTurnRequestMessage message = (EndTurnRequestMessage) MessageParser.parseMessage(json).get();

    assertSame(MessageType.END_TURN_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
    assertEquals(12, message.characterID);
  }

  @Test
  void testTransferRequestMessage() {
    String json = """
        {
          "type": "TRANSFER_REQUEST",
          "version": 1.0,
          "clientID": 1234,
          "characterID": 12,
          "targetID": 12,
          "amount": 10
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    TransferRequestMessage message = (TransferRequestMessage) MessageParser.parseMessage(json)
        .get();

    assertSame(MessageType.TRANSFER_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
    assertEquals(12, message.characterID);
    assertEquals(12, message.targetID);
    assertEquals(10, message.amount);
  }

  @Test
  void testGamestateRequestMessage() {
    String json = """
        {
          "type": "GAMESTATE_REQUEST",
          "version": 1.0,
          "clientID": 1234
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    GamestateRequestMessage message = (GamestateRequestMessage) MessageParser.parseMessage(json)
        .get();

    assertSame(MessageType.GAMESTATE_REQUEST, message.type);
    assertEquals("1.0", message.version);
    assertEquals(1234, message.clientID);
  }

  @Test
  void testPauseRequestMessage() {
    String json = """
        {
          "type": "PAUSE_REQUEST",
          "version": 1.0,
          "pause": true
        }""";

    assertTrue(MessageParser.parseMessage(json).isPresent());
    PauseRequestMessage message = (PauseRequestMessage) MessageParser.parseMessage(json).get();

    assertSame(MessageType.PAUSE_REQUEST, message.type);
    assertEquals("1.0", message.version);

    boolean pause = true;
    assertEquals(pause, message.pause);
  }
}
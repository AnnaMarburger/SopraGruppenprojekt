
//using System.Threading.Tasks.Dataflow;

using Godot;
using System;
using System.Collections.Generic;
using System.Text;
using Client.Scenes;
using Client.Scenes.Autoloads;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Editor.Scenes;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using BeneGesserit = Client.Scenes.GameClasses.BeneGesserit;
using Character = Client.Scenes.GameClasses.Character;
using Fighter = Client.Scenes.GameClasses.Fighter;
using Mentat = Client.Scenes.GameClasses.Mentat;
using Noble = Client.Scenes.GameClasses.Noble;
using Object = Godot.Object;
using System.Linq;

public class GameScreen : Node2D, Observable
{
    private ServerConnection _sc;
    private GameState _gs;
    private ScenarioMap _sm;
    private CharacterMap _cm;
    private SpiceMap _spm;
    private GameOverDialog _god;
    private SandwormMap _swm;
    private HighlightMap _hm;
    private SandstormMap _ssm;
    private CharacterPanel _cp;
    private bool isTurn = false;
    private MainCamera _cam;
    private int target;

    public override void _Ready()
    {
        _sc = GetNode<ServerConnection>("/root/ServerConnection");
        _sc.setCurrentHandler(this);
        _gs = GetNode<GameState>("/root/GameState");
        _sm = GetNode<ScenarioMap>("ScenarioMap");
        _cm = GetNode<CharacterMap>("CharacterMap");
        _spm = GetNode<SpiceMap>("SpiceMap");
        _hm = GetNode<HighlightMap>("HighlightMap");
        _cp = GetNode<CharacterPanel>("CanvasLayer/CharacterPanel");
        _ssm = GetNode<SandstormMap>("SandstormMap");
        _swm = GetNode<SandwormMap>("SandwormMap");
        _god = GetNode<GameOverDialog>("GameOverDialog");
        
        GD.Print(_sc.getID());
        _gs._playerID1 = _sc.getID();
        
        _cam = GetNode<MainCamera>("MainCamera");
        _SetCameraLimit();
        _sc.setCurrentHandler(this);
        _sc.getGameState();
        _sc.setCurrentHandler(this);
        
        
        //  //TODO remove (Sandworm Testing cause SERVER BEHINDERT)
        // _swm.placeSandworm(new Coords(0,4));
        // List<Coords> path = new List<Coords>();
        // path.Add(new Coords(0,5));
        // path.Add(new Coords(0,6));
        // path.Add(new Coords(1,6));
        // path.Add(new Coords(2,6));
        // _swm.moveSandworm(path);

        // //TODO game over dialog test
        // _god.PopUp(true);
    }


    public void parseMessage(string data)
    {
        JObject jInput = JObject.Parse(data);
        string messageType = jInput.GetValue("type").ToString();

        switch (messageType)
        {
            case "MAP_CHANGE_DEMAND":
                _changeMapDemand(data);
                break;
            case "SPAWN_CHARACTER_DEMAND":
                _spawnCharacterDemand(data);
                break;
            case "TURN_DEMAND":
                _turnDemand(data);
                break;
            case "MOVEMENT_DEMAND":
                _movementDemand(data);
                break;
            case "HELI_DEMAND":
                _heliDemand(data);
                GD.Print("Heli Demand");
                break;
            case "ATOMIC_UPDATE_DEMAND":
                _atomicUpdateDemand(data);
                break;
            case "CHARACTER_STAT_CHANGE_DEMAND":
                _characterStatChangeDemand(data);
                if(_cm._currentCharacter != null)
                    _cp.updateStats(_cm._currentCharacter);
                break;
            case "GAMESTATE":
                _gamestateRequest(data);
                break;
            case "CHANGE_PLAYER_SPICE_DEMAND":
                _changePlayerSpiceDemand(data);
                break;
            case "SANDWORM_SPAWN_DEMAND":
                _sandwormSpawnDemand(data);
                break;
            case "SANDWORM_MOVE_DEMAND":
                _sandwormMoveDemand(data);
                break;
            case "SANDWORM_DESPAWN_DEMAND":
                _sandwormDespawnDemand(data);
                break;
            case "GAME_END":
                _gameEnd(data);
                break;
            case "GAME_PAUSE_DEMAND":
                _gamePauseDemand(data);
                break;
            case "ENDGAME":
                _overlength(data);
                break;
            case "JOINACCEPTED":
                _sc.setConnected(true);
                break;
        }

    }

    public void _overlength(String data)
    {
        EndgameMesssage msg = JsonConvert.DeserializeObject<EndgameMesssage>(data);
        GetNode<Label>("overlength").Text = "overlength algorithm activated";
    }

    public void _gamePauseDemand(String data)
    {
        GamePauseDemand msg = JsonConvert.DeserializeObject<GamePauseDemand>(data);
        _sc.setpauseclientID(msg.requestedByClientID);
        GetTree().ChangeScene("res://Scenes/PauseScreen/PauseScreen.tscn");
    }

    public void _on_pauseButton_pressed()
    {
        _sc.pauseRequest(true);
    }

    private void _heliDemand(string data)
    {
        HeliDemand msg = JsonConvert.DeserializeObject<HeliDemand>(data);
        if(msg.crash)
        {
            
        }
        Character character = _gs.getCharacterById(msg.characterID);
        character.ChangePosition(msg.target);
        _hm.SetHighlight(character._pos);
        GD.Print("heliported");
    }

    private void _sandwormDespawnDemand(string data)
    {
        _swm.despawn();
        
    }

    private void _gameEnd(string data)
    {
        GameEndMessage msg = JsonConvert.DeserializeObject<GameEndMessage>(data);
        if(msg.winnerID == _gs._playerID1)
        {
            _god.PopUp(true);
        } else 
        {
            _god.PopUp(false);
        }
    }

    private void _sandwormMoveDemand(string data)
    {
        GD.Print("-----move sandworm------");
        GD.Print(data);
        SandwormMoveDemand demand = JsonConvert.DeserializeObject<SandwormMoveDemand>(data);
        _gs.getCharacterById(target)._stats.HP=1;
        _swm.moveSandworm(demand.path,target);
        
    }

    private void _sandwormSpawnDemand(string data)
    {
        GD.Print(what: "------spawn sandworm-----");
        GD.Print(data);
        SandwormSpawnDemand demand = JsonConvert.DeserializeObject<SandwormSpawnDemand>(data);
        _swm.placeSandworm(demand.position);
      
        target = demand.characterID;
    }

    private void _changePlayerSpiceDemand(string data)
    {
        ChangePlayerSpiceDemand demand = JsonConvert.DeserializeObject<ChangePlayerSpiceDemand>(data);
        var playerInfo = _sc.getGameCfg().citiesToClients;
        
        _gs.updateSpice(demand.clientID,demand.newSpiceValue);
        GetNode<Label>("CanvasLayer/GameStats/Spice_Player1").Text = _sc.nickname+ ": " +_gs.getPlayer1Spice();
        if(playerInfo[0].clientName.Equals(_sc.nickname))
        GetNode<Label>("CanvasLayer/GameStats/Spice_Player2").Text = playerInfo[1].clientName+ ": " +_gs.getPlayer2Spice();
        else
        {
            GetNode<Label>("CanvasLayer/GameStats/Spice_Player2").Text = playerInfo[0].clientName+ ": " +_gs.getPlayer2Spice();
        }
    }

    public void _gamestateRequest(string msg)
    {
        Gamestate gs = JsonConvert.DeserializeObject<Gamestate>(msg);
        foreach (var s in gs.history)
        {
            parseMessage(s);
        }
    }
    
    public void _characterStatChangeDemand(string data)
    {
        CharacterStatChangeDemand demand = JsonConvert.DeserializeObject<CharacterStatChangeDemand>(data);
        _gs.updateCharacterStats(demand.characterID, demand.stats);
        _cm.SetCharacters();
         //update UI
        Character c = this._gs.getCharacterById(demand.characterID);
        if(_gs.getCharacterById(demand.characterID)._stats.HP==0)
            GD.Print("dead");
        _cp.Hide();
        if (isTurn){
            _cp.PopUp(c, _sm.scenario[c._pos.y, c._pos.x], _gs.getPlayer2Characters().Values.ToList());
            GD.Print("updated character button");
        }
    }

    public void _atomicUpdateDemand(string data)
    {
        AtomicsUpdateDemand demand = JsonConvert.DeserializeObject<AtomicsUpdateDemand>(data);
        _gs.updateAtomic(demand);
    }

    public void _turnDemand(string msg)
    {
        
        TurnDemand turnDemand = JsonConvert.DeserializeObject<TurnDemand>(msg);
        if (turnDemand.clientID != _sc.getID())
        {
            _hm.SetHighlight(_gs.getCharacterById(turnDemand.characterID)._pos);
            isTurn = false;
            _cp.Hide();
        }
        else
        {

            isTurn = true;
            var coords = this._gs.getCharacterById(turnDemand.characterID)._pos;
            _cm.SetCurrentCharacter(turnDemand.characterID);
            _hm.SetHighlight(coords);
            Character c = this._gs.getCharacterById(turnDemand.characterID);
            _cp.PopUp(c, _sm.scenario[c._pos.x, c._pos.y], _gs.getPlayer2Characters().Values.ToList());
        }
    }

    public void _movementDemand(string msg)
    {
        MovementDemand movDemand = JsonConvert.DeserializeObject<MovementDemand>(msg);
        if (movDemand.specs.path.Count == 0)
            return;
        var characterById = _gs.getCharacterById(movDemand.characterID);
        GD.Print(characterById._pos.x + " " + characterById._pos.y);
        GD.Print(movDemand.specs.path[movDemand.specs.path.Count - 1]);
        characterById.ChangePosition(movDemand.specs.path[movDemand.specs.path.Count - 1]);
        _cm.UpdateCharacters();

        //update UI
        _hm.SetHighlight(characterById._pos);
        Character c = this._gs.getCharacterById(movDemand.characterID);
        _cp.Hide();
        if (isTurn){
            _cp.PopUp(c, _sm.scenario[c._pos.y, c._pos.x], _gs.getPlayer2Characters().Values.ToList());
            GD.Print("updated character button");
        }
    }


    private void _changeMapDemand(string msg)
    {
        MapChangeDemand demand = JsonConvert.DeserializeObject<MapChangeDemand>(msg);
        List<List<Tile>> newMap = demand.newMap;
        Tile[,] tiles = new Tile[newMap.Count, newMap[0].Count];
        for (int x = 0; x < newMap.Count; x++)
        {
            for (int y = 0; y < newMap[0].Count; y++)
            {
                tiles[x, y] = newMap[x][y];
            }
        }

        _gs.updateScenario(tiles);

        // show/update tiles (map) visually
        _sm.SetTiles();
        // show/update spice on tiles visually
        _spm.SetSpiceOnTiles();
        // set current Spice
        GD.Print(_sc.getGameCfg());
        var playerInfo = _sc.getGameCfg().citiesToClients;
        GetNode<Label>("CanvasLayer/GameStats/Spice_Player1").Text = playerInfo[0].clientName + ": " + _gs.getPlayer1Spice();
        GetNode<Label>("CanvasLayer/GameStats/Spice_Player2").Text = playerInfo[1].clientName + ": " + _gs.getPlayer2Spice();

        //update Sandstorm
        _ssm.updateSandstorm(demand.stormEye, _sm.width, _sm.height);
    }

    private void _spawnCharacterDemand(string msg)
    {
        //complete character
        var spawnCharacterDemand = JsonConvert.DeserializeObject<SpawnCharacterDemand>(msg);
        //character attributes
        var attributes = spawnCharacterDemand.attributes;


        //Get current HP; MP AND AP from character, not loud, not eaten
        Stats stats = new Stats(attributes.healthCurrent, attributes.APcurrent, attributes.MPcurrent
            , 0, false, false);
        //Creates character with stats, position and name

        switch (attributes.characterType)
        {
            case CharacterClass.MENTAT:
                _gs.addCharacter(spawnCharacterDemand.clientID, spawnCharacterDemand.characterID,
                    new Mentat(stats, spawnCharacterDemand.position, spawnCharacterDemand.characterName,
                        spawnCharacterDemand.clientID, spawnCharacterDemand.characterID));
                break;
            case CharacterClass.FIGHTER:
                _gs.addCharacter(spawnCharacterDemand.clientID, spawnCharacterDemand.characterID,
                    new Fighter(stats, spawnCharacterDemand.position, spawnCharacterDemand.characterName,
                        spawnCharacterDemand.clientID, spawnCharacterDemand.characterID));
                break;
            case CharacterClass.BENE_GESSERIT:
                _gs.addCharacter(spawnCharacterDemand.clientID, spawnCharacterDemand.characterID,
                    new BeneGesserit(stats, spawnCharacterDemand.position, spawnCharacterDemand.characterName,
                        spawnCharacterDemand.clientID, spawnCharacterDemand.characterID));
                break;
            case CharacterClass.NOBLE:
                _gs.addCharacter(spawnCharacterDemand.clientID, spawnCharacterDemand.characterID,
                    new Noble(stats, spawnCharacterDemand.position, spawnCharacterDemand.characterName,
                        spawnCharacterDemand.clientID, spawnCharacterDemand.characterID));
                break;
        }

        // show/update characters visually
        _cm.SetCharacters();
    }


    private void _movementRequest(Character c, Coords newPos)
    {
        List<Coords> path = _sm.getPath(newPos, c._pos, c._stats.MP);
        if (path == null)
        {
            GD.Print("Invalid Path");
        }
        else
        {
            GD.Print("Found path: ");
            foreach (var coord in path)
            {
                GD.Print(coord.ToString());
            }

            SpecsMovement specs = new SpecsMovement(path);

            _sc.moveCharacter(c.characterID, specs);
        }

        _cm.moving = false;
    }


    public override void _UnhandledInput(InputEvent @event)
    {
        // Mouse in viewport coordinates.
        if (@event.IsActionPressed("left_click"))
        {
            GD.Print("button pressed");
            Character currentC = _cm.GetCurrentCharacter();
            GD.Print((currentC != null) + "  " + _cm.moving);
            if (currentC != null && _cm.moving)
            {
                GD.Print("field to move to chosen");
                Vector2 pos = _cm.WorldToMap(GetGlobalMousePosition());
                GD.Print(pos.x + " " + pos.y);
                _movementRequest(currentC, new Coords((int)pos.x, (int)pos.y));
            }
            else if (_cm.GetAtomics())
            {
                GD.Print("Field to attack chosen");
                Vector2 pos = _cm.WorldToMap(GetGlobalMousePosition());
                _sc.familyAtomics(currentC.characterID, new Coords((int)pos.x, (int)pos.y));
            }
            else if (_cm.getAttack())
            {
                GD.Print("Field to attack chosen");
                Vector2 pos = _cm.WorldToMap(GetGlobalMousePosition());
                _sc.attackChracter(currentC.characterID, new Coords((int)pos.x, (int)pos.y));
            }
            else if(_cm.getHeli())
            {
                GD.Print("Field to heli port to chosen");
                Vector2 pos = _cm.WorldToMap(GetGlobalMousePosition());
                _sc.heliPort(currentC.characterID,new Coords((int)pos.x, (int)pos.y));
            }
            else if (_cm.getDonate())
            {
                GD.Print("character to donate to chosen");
                Vector2 pos = _cm.WorldToMap(GetGlobalMousePosition());
                try
                {
                    var characterIdByPosition = _gs.getCharacterIDByPosition(new Coords((int)pos.x, (int)pos.y));
                    _sc.transferSpice(currentC.characterID,characterIdByPosition,_cm.GetSpiceAmount());
                }
                catch (Exception e)
                {
                    Console.WriteLine(e);
                }
            }

            _cm.reset();
        }
    }
    
    public void _on_skipButton_pressed()
    {
        _sc.skipTurn(_cm.GetCurrentCharacter().characterID);
    }


    public void _on_CharacterPanel_hide()
    {
        GD.Print("Attempting to close panel");
        if (isTurn)
            _cp.Hide();
        else
        {
            return;
        }
    }


    public void _on_collectButton_pressed()
    {
        Character c = _cm.GetCurrentCharacter();
        _sc.collectSpice(c.characterID, c._pos);
    }


    private void _SetCameraLimit()
    {
        // var boundaries = _sm.GetUsedRect();
        // GD.Print("Area" + boundaries.Area);
        // var cellSize = _sm.CellSize;
        // _cam.LimitLeft = (int)Math.Round(boundaries.Position.x * cellSize.x);
        // _cam.LimitRight = (int)Math.Round(boundaries.End.x * cellSize.x);
        // _cam.LimitTop = (int)Math.Round(boundaries.Position.y * cellSize.y);
        // _cam.LimitBottom = (int)Math.Round(boundaries.End.y * cellSize.y);
    }


}
using Godot;
using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using Client.Scenes;
using Client.Scenes.Autoloads;
using System.Threading;
using Client.Scenes.NetComClasses;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Error = Godot.Error;
using Thread = System.Threading.Thread;
using Client.Scenes.GameClasses;


public class UserInput : WindowDialog, Observable
{

	private string _nickName;
	private string _ipAddress;
	public string _portNo;
	private bool _role;

	private readonly string _defaultNickname = "ImStupid";
	private readonly string _defaultPort = "10191";
	private readonly string _defaultIp = "127.0.0.1";
	
	private ServerConnection _permaConnection;
	
	private JoinAccepted _joinAcceptedJson;
	private HouseOffer _houseOfferJson;

	private Client.Scenes.NetComClasses.Error _errorJson;
	private GameCfg _gameCfgJson;
	private GameState _gs;

	// Called when the node enters the scene tree for the first time.
	public override void _Ready()
	{
		_permaConnection = GetNode<ServerConnection>("/root/ServerConnection");
		_permaConnection.setCurrentHandler(this);
		
		//TODO vor Abnahme defaultNickname wegmachen, dafür _nickName auf "" initialisieren (defaultNickName nur sinnvoll für debugging)
		_nickName = _defaultNickname;
		_ipAddress = _defaultIp;
		_portNo = _defaultPort;
		_gs= GetNode<GameState>("/root/GameState");
	}

	public void _on_JoinGameButton_pressed()
	{
		this.Popup_();
	}
	public void _on_NicknameInput_text_changed(string newNickname)
	{
		_nickName = newNickname;
	}

	public void _on_IPAddressInput_text_changed(string newIpAddress)
	{
		_ipAddress = newIpAddress;
	}
	
	public void _on_PortNoInput_text_changed(string newPortString)
	{
		_portNo = newPortString;
	}

	public void _on_JoinAsSpectator_pressed()
	{
		_role = false;
		connectToServer();
	}
	
	public void _on_JoinAsPlayer_pressed()
	{
		_role = true;
		connectToServer();
		
	}

	public String toString()
	{
		return "´\nNickname: " + _nickName +
			   "\nIP-Address: " + _ipAddress +
			   "\nPort No: " + _portNo +
			   "\nSelected role: player" + _role;
	}
	/**
	 * let check the user input and let establish the websocket connection if user input is valid
	 */
	private void connectToServer()
	{
		if (!CheckInput())
		{
			return;
		}
		
		_permaConnection.setIP(_ipAddress);
		_permaConnection.setPort(_portNo);

		Boolean joinedGame = _permaConnection.joinGame(_nickName,_ipAddress,_portNo,_role);
		GD.Print(joinedGame);
		
		if (!joinedGame)
		{
			GD.Print("Failed to join game");
			return;
		}
		
		GD.Print("Connection established");
		//GD.Print(ws.GetConnectedHost());
	}

	public void changeToHSScreen()
	{
		GetNode<HouseSelection>("/root/HouseSelection").house = _houseOfferJson;
		GD.Print(_houseOfferJson.houses[0].houseName);
		GetTree().ChangeScene("res://Scenes/HouseSelection/HouseSelectionScreen.tscn");
	}

	public void changeToGameScreen()
	{
		//DEBUG AUSGABE
		GD.Print("Spectator Changing to Gamescreen");
		GetTree().ChangeScene("res://Scenes/GameScreen/GameScreen.tscn");
	}
	
/**
 * reads in the user input and validifies it and if valid reads in
 */
	private Boolean CheckInput()
	{
		Boolean inputValid = true;
		
		Match inputMatch = Regex.Match(_nickName, "(\\w|\\d){2}");
		if (!inputMatch.Success)
		{
			inputValid = false;
		}
		
		inputMatch = Regex.Match(_ipAddress, "^(\\d{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})$");
		if (!inputMatch.Success)
		{
			inputValid = false;
		}
		
		inputMatch = Regex.Match(_portNo, "^\\d+$");
		if (!inputMatch.Success)
		{
			inputValid = false;
		}

		if (inputValid)
		{
			Console.WriteLine("===User input valid===");
		}
		else
		{
			Console.WriteLine("===User input not valid===");
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * parses the incoming messages and calls the methods to work with them
	 */
	public void parseMessage(string data)
	{
		
		JObject jInput = JObject.Parse(data);

		string messageType = jInput.GetValue("type").ToString();
		GD.Print(messageType);
			
		switch (messageType)
		{
			case "HOUSE_OFFER":
				_houseOffer(data);
				break;
			case "JOINACCEPTED":
				_joinAccepted(data);
				break;
			case "GAMECFG":
				_gamecfg(data);
				break;
			case "ERROR":
				_error(data);
				break;
			default:
				break;
		}
		
		
	}

/**
 * takes the houseOfferDemandMessage and handles this
 */
	private void _houseOffer(string msg)
	{
		_houseOfferJson = JsonConvert.DeserializeObject<HouseOffer>(msg);
		if(_houseOfferJson.clientID==_permaConnection.getID())
			changeToHSScreen();
		GD.Print(_houseOfferJson);
	}

/**
 * prints error messages
 */
	private void _error(string msg)
	{
		_errorJson = JsonConvert.DeserializeObject<Client.Scenes.NetComClasses.Error>(msg);
		GD.Print(_errorJson);
	}

/**
 * handles the gamecfg message
 */
	private void _gamecfg(string msg)
	{
		GD.Print(msg);
		_gameCfgJson = JsonConvert.DeserializeObject<GameCfg>(msg);
		_permaConnection.setGameCfg(_gameCfgJson);
		GD.Print(_gameCfgJson);
		if(_permaConnection.getID() == _gameCfgJson.citiesToClients[0].clientID)
		{
			
			_gs._playerID2= _gameCfgJson.citiesToClients[1].clientID;
		}
		else if(_permaConnection.getID() == _gameCfgJson.citiesToClients[1].clientID){
		
			_gs._playerID2= _gameCfgJson.citiesToClients[0].clientID;
		}
		
	}
	
/**
 * handles joinAcceptedMessage
 */
private void _joinAccepted(string msg)
	{
		_joinAcceptedJson = JsonConvert.DeserializeObject<JoinAccepted>(msg);
		_permaConnection.setID(_joinAcceptedJson.clientID);
		_permaConnection.setSecret(_joinAcceptedJson.clientSecret);
		_gs._playerID1 = _joinAcceptedJson.clientID;
		GD.Print(_joinAcceptedJson.clientID+" meine ID");
		if(_role == false)
		{
			List<Client.Scenes.NetComClasses.Client> tem = new List<Client.Scenes.NetComClasses.Client>();
			tem.Add(new Client.Scenes.NetComClasses.Client("player A"));
			tem.Add(new Client.Scenes.NetComClasses.Client("player B"));
			_permaConnection.setID(0);
			_permaConnection.setGameCfg(new GameCfg(null,null,tem,null));
			changeToGameScreen();
		}
	}
}

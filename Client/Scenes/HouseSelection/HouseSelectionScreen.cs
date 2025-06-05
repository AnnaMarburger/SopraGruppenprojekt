using Godot;
using System;
using Client.Scenes;
using Client.Scenes.Autoloads;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using BeneGesserit = Client.Scenes.NetComClasses.BeneGesserit;
using Character = Client.Scenes.GameClasses.Character;
using Fighter = Client.Scenes.NetComClasses.Fighter;
using Mentat = Client.Scenes.NetComClasses.Mentat;
using Noble = Client.Scenes.NetComClasses.Noble;

public class HouseSelectionScreen : Node2D, Observable
{

	public HouseOffer _hs;

	private House house1;
	private House house2;
	private ServerConnection _sc;

	private GameState _gs;

	// Called when the node enters the scene tree for the first time.
	public override void _Ready()
	{
		_sc = GetNode<ServerConnection>("/root/ServerConnection");
		_hs = GetNode<HouseSelection>("/root/HouseSelection").house;
		_gs = GetNode<GameState>("/root/GameState");
		_sc.setCurrentHandler(this);
		
		house1 = _hs.houses[0];
		house2 = _hs.houses[1];

		//adjust Screen to represent both houses to chose from
		Godot.Label house1_label = this.GetNode<Godot.Label>("House1_label");
		house1_label.Text = house1.houseName.ToString();
	
		for(int i = 0; i<6; i++)
		{
			//change sprites
			string tmp = "";
			
			switch(house1.houseMessageCharacters[i].characterClass) 
			{
				case CharacterClass.NOBLE:
					tmp = "res://Assets/Noble.png";
					break;
				case CharacterClass.BENE_GESSERIT:
					tmp = "res://Assets/BeneGesserit.png";
					break;
				case CharacterClass.MENTAT:
					tmp = "res://Assets/Mentat.png";
					break;
				case CharacterClass.FIGHTER:
					tmp = "res://Assets/Fighter.png";
					break;
			}
			var tex = (Texture)GD.Load(tmp);
			house1_label.GetNode<Godot.Sprite>("House1_character"+(i+1)).Texture = tex;

			//change character names
			house1_label.GetNode<Godot.Label>("House1_character"+(i+1)+"_label").Text = house1.houseMessageCharacters[i].characterName;
		}

		Godot.Label house2_label = this.GetNode<Godot.Label>("House2_label");
		house2_label.Text = house2.houseName.ToString();
		for(int i = 0; i<6; i++)
		{
			//change sprites
			string tmp = "";
			
			switch(house2.houseMessageCharacters[i].characterClass) 
			{
				case CharacterClass.NOBLE:
					tmp = "res://Assets/Noble.png";
					break;
				case CharacterClass.BENE_GESSERIT:
					tmp = "res://Assets/BeneGesserit.png";
					break;
				case CharacterClass.MENTAT:
					tmp = "res://Assets/Mentat.png";
					break;
				case CharacterClass.FIGHTER:
					tmp = "res://Assets/Fighter.png";
					break;
			}
			var tex = (Texture)GD.Load(tmp);
			house2_label.GetNode<Godot.Sprite>("House2_character"+(i+1)).Texture = tex;

			//change character names
			house2_label.GetNode<Godot.Label>("House2_character"+(i+1)+"_label").Text = house2.houseMessageCharacters[i].characterName;
		}




	}


	public void _on_House1_Button_pressed()
	{
		HouseRequest houseRequest = new HouseRequest(house1.houseName);
		_sc.sendMessage(JObject.FromObject(houseRequest).ToString());
		changeScene();
		
	}
	
	public void _on_House2_Button_pressed()
	{
		HouseRequest houseRequest = new HouseRequest(house2.houseName);
		_sc.sendMessage(JObject.FromObject(houseRequest).ToString());
		changeScene();
		
	}

	public void parseMessage(string data)
	{
		
		JObject jInput = JObject.Parse(data);

		string messageType = jInput.GetValue("type").ToString();
		
		switch (messageType)
		{
			case "SPAWN_CHARACTER_DEMAND":
				_spawnCharacterDemand(data);
				break;
			
		}
	}

	public void changeScene()
	{
		GetTree().ChangeScene("res://Scenes/GameScreen/GameScreen.tscn");
	}
	
	private void _spawnCharacterDemand(string msg)
	{
		//complete character
		var spawnCharacterDemand = JsonConvert.DeserializeObject<SpawnCharacterDemand>(msg);
		//character attributes
		var attributes = spawnCharacterDemand.attributes;
		
		//Get current HP; MP AND AP from character, not loud, not eaten
		Stats stats = new Stats(attributes.healthCurrent,attributes.APcurrent,attributes.MPcurrent
			,0,false,false);

		//Creates character with stats, position and name
		switch (attributes.characterType)
		{
			case CharacterClass.MENTAT:
				
				_gs.addCharacter(spawnCharacterDemand.clientID,spawnCharacterDemand.characterID,
					new Client.Scenes.GameClasses.Mentat(stats,spawnCharacterDemand.position,spawnCharacterDemand.characterName,spawnCharacterDemand.clientID,spawnCharacterDemand.characterID));
				break;
			case CharacterClass.FIGHTER:
				
				_gs.addCharacter(spawnCharacterDemand.clientID,spawnCharacterDemand.characterID,
					new Client.Scenes.GameClasses.Fighter(stats,spawnCharacterDemand.position,spawnCharacterDemand.characterName,spawnCharacterDemand.clientID,spawnCharacterDemand.characterID));
				break;
			case CharacterClass.BENE_GESSERIT:
				
				_gs.addCharacter(spawnCharacterDemand.clientID,spawnCharacterDemand.characterID,
					new Client.Scenes.GameClasses.BeneGesserit(stats,spawnCharacterDemand.position,spawnCharacterDemand.characterName,spawnCharacterDemand.clientID,spawnCharacterDemand.characterID));
				break;
			case CharacterClass.NOBLE:
				
				_gs.addCharacter(spawnCharacterDemand.clientID,spawnCharacterDemand.characterID,
					new Client.Scenes.GameClasses.Noble(stats,spawnCharacterDemand.position,spawnCharacterDemand.characterName,spawnCharacterDemand.clientID,spawnCharacterDemand.characterID));
				break;
		}

	}
}

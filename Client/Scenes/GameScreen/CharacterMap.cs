using System.Globalization;
using Godot;
using System;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Client.Scenes.Autoloads;
using Character = Client.Scenes.GameClasses.Character;
using System.Collections.Generic;

public class CharacterMap : TileMap
{
	private GameState _gs;
	Dictionary<int,Character> player1Characters;
	Dictionary<int,Character> player2Characters;
	public Character _currentCharacter;
	public bool moving;
	private bool atomics=false;
	private bool attack;
	private bool donate;
	private ServerConnection _sc;
	private bool kanly;
	private bool heli;

	private SpiceAmount _sa;
	private int _spiceAmount = 0;

	// Called when the node enters the scene tree for the first time.
	public override void _Ready()
	{
		_sc = GetNode<ServerConnection>("/root/ServerConnection");
		_sa = GetNode<SpiceAmount>("/root/GameScreen/CanvasLayer/CharacterPanel/SpiceAmount");
		_sa.Text = "0";
	}

	public Character GetCurrentCharacter()
	{
		return _currentCharacter;
	}

	//Places all Characters visually on their position on the map
	public void SetCharacters()
	{
		Clear();
		_gs = GetNode<GameState>("/root/GameState");
		player1Characters = _gs.getPlayer1Characters();
		player2Characters = _gs.getPlayer2Characters();
		GD.Print("p1 "+player1Characters.Count);
		GD.Print("p2 "+ player2Characters.Count);

			//placing characters of player 1
			foreach (var _char in player1Characters.Values)
			{
				int typeInt = 0;
				GD.Print(_char._stats.HP);
				if (_char._stats.HP == 0)
				{
					GD.Print("skip");
					continue;
				}
				switch(_char._class){
					case(CharacterClass.MENTAT):
						typeInt = 4;
						break;
					case(CharacterClass.NOBLE):
						typeInt = 6;
						break;
					case(CharacterClass.BENE_GESSERIT):
						typeInt =0;
						break;
					case(CharacterClass.FIGHTER):
						typeInt = 2;
						break;
				}
				if(_char._stats.HP!=0)
					SetCell(_char._pos.x, _char._pos.y, typeInt);
			}

			//placing characters of player 2
			foreach (var _char in player2Characters.Values)
			{
				GD.Print(_char._stats.HP);
				int typeInt = 0;
				if (_char._stats.HP == 0)
				{
					continue;
				}
					
				switch(_char._class){
					case(CharacterClass.MENTAT):
						typeInt = 5;
						break;
					case(CharacterClass.NOBLE):
						typeInt = 7;
						break;
					case(CharacterClass.BENE_GESSERIT):
						typeInt = 1;
						break;
					case(CharacterClass.FIGHTER):
						typeInt = 3;
						break;
				}
				
					SetCell(_char._pos.x, _char._pos.y, typeInt);
			}

	}

	public void UpdateCharacters()
	{
		
		SetCharacters();
	}


	//Sets the current character (on null, if there isn't one on the field)
	public void SetCurrentCharacter(int characterID)
	{
		this._currentCharacter=_gs.getCharacterById(characterID);
	}

	public void _on_moveButton_pressed()
	{
		GD.Print("moving true");
		moving = true;
	}


	public void _on_specialButton2_pressed()
	{
		GD.Print("targeting true");
		atomics = true;
	}

	public void _on_attackButton_pressed()
	{
		GD.Print("attacking true");
		attack= true;
	}

	public void _on_donateButton_pressed()
	{
		GD.Print("spiceDonating true");
		
		donate= true;
	}
	
	public bool GetAtomics()
	{
		return atomics;
	}

	public bool getDonate()
	{
		return donate;
	}

	public bool getAttack()
	{
		return attack;
	}

	
	public void reset()
	{
		this.moving = false;
		this.atomics = false;
		this.attack = false;
		this.heli = false;
	}

	
	public void _on_specialButton1_pressed()
	{
		reset();
		Character c = GetCurrentCharacter();
		switch (c._class)
		{
			case CharacterClass.BENE_GESSERIT:
				_sc.voiceAttack(c.characterID,c._pos);
				break;
			case CharacterClass.FIGHTER:
				_sc.spinToWin(c.characterID,c._pos);
				break;
			case CharacterClass.MENTAT:
				_sc.spiceHoarding(c.characterID,c._pos);
				break;
			case CharacterClass.NOBLE:
				kanly = true;
				break;
		}
	}

	public void _on_SpiceAmount_text_changed(string new_text)
	{
		var input = new_text;
		if (!int.TryParse(input, out _spiceAmount))
			_spiceAmount = 0;

	}

	public bool getHeli()
	{
		return heli;
	}
	public int GetSpiceAmount()
	{
		return _spiceAmount;
	}

	public void _on_heliButton_pressed()
    {
		GD.Print("Heli Button pressed");
        this.heli = true;
    }

	
}

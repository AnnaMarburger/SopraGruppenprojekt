using Godot;
using System;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Client.Scenes.Autoloads;
using Character = Client.Scenes.GameClasses.Character;
using System.Collections.Generic;

public class CharacterPanel : Container
{
	// Called when the node enters the scene tree for the first time.
	public override void _Ready()
	{

	}

	public void PopUp(Character _character, Tile tile, List<Character> enemies)
	{
		// check if heliport possible
		Button heliButton = GetNode<Button>("heliButton");
		if(tile.MessageTileType == messageTileType.HELIPORT)
		{
			heliButton.Disabled = false;
		} else 
		{
			heliButton.Disabled = true;
		}

		//check if collect possible
		Button collectButton = GetNode<Button>("collectButton");
		if(tile.hasSpice)
		{
			collectButton.Disabled = false;
			GD.Print("Character can now collect spice");
		} else 
		{
			collectButton.Disabled = true;
		}

		//check if attack possbile
		Button attacktButton = GetNode<Button>("attackButton");
		List<Coords> neighbourFields = new List<Coords>();
        neighbourFields.Add(new Coords(_character._pos.x-1, _character._pos.y-1));
        neighbourFields.Add(new Coords(_character._pos.x-1, _character._pos.y));
        neighbourFields.Add(new Coords(_character._pos.x, _character._pos.y-1));
        neighbourFields.Add(new Coords(_character._pos.x-1, _character._pos.y+1));
        neighbourFields.Add(new Coords(_character._pos.x+1, _character._pos.y-1));
        neighbourFields.Add(new Coords(_character._pos.x+1, _character._pos.y));
        neighbourFields.Add(new Coords(_character._pos.x, _character._pos.y+1));
        neighbourFields.Add(new Coords(_character._pos.x+1, _character._pos.y+1));
		bool canAttack = false;
		foreach (Character _c in enemies)
		{
			foreach(Coords coord in neighbourFields){
				if(_c._pos.Equals(coord)){
					GD.Print("enemy nearby");
					canAttack=true;
					break;
				}
			}
		}
		if(canAttack)
		{
			attacktButton.Disabled = false;
			GD.Print("Character can now attack enemy");
		}
		else
			attacktButton.Disabled = true;


		// update special action buttons according to character type
		Button specialAction1 = GetNode<Button>("specialButton1");
		Button specialAction2 = GetNode<Button>("specialButton2");

		switch(_character._class) {
			case(CharacterClass.MENTAT):
				specialAction1.Text = "Spice Hoarding";
				specialAction2.Text = "-";
				specialAction2.Disabled = true;
				break;
			case(CharacterClass.NOBLE):
				specialAction1.Text = "Kanly";
				specialAction2.Text = "Family Atomics";
				specialAction2.Disabled = false;
				break;
			case(CharacterClass.BENE_GESSERIT):
				specialAction1.Text = "Voice";
				specialAction2.Text = "-";
				specialAction2.Disabled = true;
				break;
			case(CharacterClass.FIGHTER):
				specialAction1.Text = "Sword Spin";
				specialAction2.Text = "-";
				specialAction2.Disabled = true;
				break;
			
		}

		updateStats(_character);
		this.Show();

	}

	public void updateStats(Character _character)
	{
		GetNode<Label>("CharacterStats/name").Text = _character._name;

		GetNode<Label>("CharacterStats/AP").Text = "AP: "+_character._stats.AP;
		GetNode<Label>("CharacterStats/HP").Text = "HP: "+_character._stats.HP;
		GetNode<Label>("CharacterStats/MP").Text = "MP: "+_character._stats.MP;
		GetNode<Label>("CharacterStats/spice").Text = ": "+_character._stats.spice;
	}

	
}

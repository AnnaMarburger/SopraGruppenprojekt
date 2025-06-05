using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using Client.Scenes.NetComClasses;
using Godot;

namespace Client.Scenes.GameClasses
{
	public class GameState : Node
	{
		private Tile[,] _scenario;
		private Dictionary<int,Character> _player1Characters=new Dictionary<int, Character>();
		private Dictionary<int,Character> _player2Characters=new Dictionary<int, Character>();
		private int _player1Spice;
		private int _player2Spice;
		private int _player1Atomics;
		private int _player2Atomics;
		public int _playerID1 { get; set; }
		public int _playerID2 {get;set;}
		
		private int _clientSecret;
		private Coords _stormEye;


		public Tile[,] getScenario()
		{
			return this._scenario;
		}  

		public int getPlayer1Spice(){
			return _player1Spice;
		} 

		public int getPlayer2Spice(){
			return _player2Spice;
		} 

		public Dictionary<int,Character> getPlayer1Characters()
		{
			return this._player1Characters;
		} 

		public Dictionary<int,Character> getPlayer2Characters()
		{
			return this._player2Characters;
		} 

		public void updateScenario(Tile[,] scenario)
		{
			this._scenario = scenario;
		}

		public void updateSpice(int playerID, int spice)
		{
			if (playerID == _playerID1)
				_player1Spice = spice;
			else
				_player2Spice = spice;
		}

		public void eatCharacter(int characterID)
		{
			var character = getCharacterById(characterID);
			character.eaten();
		}

		public void killCharacter(int characterID)
		{
			var character = getCharacterById(characterID);
			character.killed();
		}

		public void reviveCharacter(int characterID, Coords pos)
		{
			var character = getCharacterById(characterID);
			character.revived(pos);
		}

		public void updateCharacterStats(int characterID, Stats stats)
		{
			var character = getCharacterById(characterID);
			if (character != null) character.updateStats(stats);
			
		}

		public Character getCharacterById(int characterID)
		{
			Character character;
			_player1Characters.TryGetValue(characterID, out character);
			if (character != null)
				return character;
			_player2Characters.TryGetValue(characterID, out character);
			return character;
		}

		public void moveCharacter(int characterID, Coords targetTile)
		{
			var character = getCharacterById(characterID);
			character.ChangePosition(targetTile);
		}

		public void addCharacter(int playerID, int characterID, Character character)
		{
			if (_playerID1 == playerID)
			{
				if (_player1Characters.ContainsKey(characterID))
					_player1Characters.Remove(characterID);
				_player1Characters.Add(characterID,character);
			}
			else
			{
				if (_player2Characters.ContainsKey(characterID))
					_player2Characters.Remove(characterID);
				_player2Characters.Add(characterID,character);
			}
				

			if(getCharacterById(characterID)==null)
				GD.Print(false);
		}

		public void updateSpiceOnCharacter(int characterID, int spice)
		{
			var character = getCharacterById(characterID);
			character.updateSpice(spice);
		}

		public int getCharacterIDByPosition(Coords pos)
		{
			foreach (var value in _player1Characters.Values)
			{
				if (value._pos.Equals(pos))
				{
					return value.characterID;
				}
			}
			foreach (var value in _player2Characters.Values)
			{
				if (value._pos.Equals(pos))
				{
					return value.characterID;
				}
			}

			throw new Exception("No Character with ID found");
		}
		public void updateAtomic(AtomicsUpdateDemand aud)
		{
			if (aud.clientID == _playerID1)
			{
				_player1Atomics = aud.atomicsLeft;
			}
			else
			{
				_player2Atomics = aud.atomicsLeft;
			}
		}
	}
	
	
}

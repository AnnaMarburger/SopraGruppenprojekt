using System.Runtime.CompilerServices;
using System.Threading;
using System;
using System.Globalization;
using Client.Scenes.NetComClasses;

namespace Client.Scenes.GameClasses
{
	public class Character
	{
		protected int _clientID;
		public House _house;
		public Stats _stats;
		protected bool _alive;
		protected bool _eatenBySandworm;
		public Coords _pos;
		public string _name;
		public CharacterClass _class;
		public int characterID;
		
		public Character(Stats stats,Coords pos, string name, int _clientId, int characterID)
		{
			this._stats = stats;
			this._alive = true;
			this._eatenBySandworm = false;
			this._pos = pos;
			this._name = name;
			this._clientID = _clientId;
			this.characterID=characterID;
		}

		public override string ToString()
		{
			return _class + " " + _name+" "+_clientID;
		}

		public void updateStats(Stats stats)
		{
			this._stats = stats;
		}

		public void eaten()
		{
			_alive = false;
			_eatenBySandworm = true;
		}

		public void killed()
		{
			_alive = false;
		}

		public void revived(Coords pos)
		{
			_alive = true;
			_eatenBySandworm = false;
			this._pos = pos;
		}

		public void ChangePosition(Coords targetTile)
		{
			this._pos = targetTile;
		}

		public void updateSpice(int spice)
		{
			this._stats.spice = spice;
		}
	}


	public class Mentat:Character
	{
		public Mentat(Stats stats, Coords pos, string name, int clientid, int characterID) : base(stats,pos,name,clientid,characterID)
		{
			this._class = CharacterClass.MENTAT;
		}
	}

	public class BeneGesserit : Character
	{
		public BeneGesserit(Stats stats, Coords pos, string name, int clientid, int characterID) : base(stats,pos,name,clientid,characterID)
		{
			this._class = CharacterClass.BENE_GESSERIT;
		}
	}

	public class Fighter : Character
	{
		public Fighter(Stats stats, Coords pos, string name, int clientid, int characterID) : base(stats,pos,name,clientid,characterID)
		{
			this._class = CharacterClass.FIGHTER;
		}
	}

	public class Noble : Character
	{
		public Noble(Stats stats, Coords pos, string name, int clientid, int characterID) : base(stats,pos,name,clientid,characterID)
		{
			this._class = CharacterClass.NOBLE;
		}
	}
}

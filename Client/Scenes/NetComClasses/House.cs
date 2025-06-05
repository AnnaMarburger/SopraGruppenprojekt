using System;
using System.Collections.Generic;
using System.ComponentModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{
	public enum HouseColor
	{
		GOLD,
		GREEN,
		RED,
		BLUE,
		SILVER,
		VIOLET
	}

	public enum HouseName
	{
		CORRINO,
		ATREIDES,
		HARKONNEN,
		ORDOS,
		RICHESE,
		VERNIUS
	}

	public enum CharacterClass
	{
		NOBLE,
		MENTAT,
		BENE_GESSERIT,
		FIGHTER,
		
	}

	public class Character
	{
		public string characterName { get; set; }
		[JsonConverter(typeof(StringEnumConverter))]
		public CharacterClass characterClass { get; set; }
		public override string ToString()
		{
			return characterName + " " + characterClass.ToString();
		}
	}
	
	public class House
	{
		[JsonConverter(typeof(StringEnumConverter))]
		public HouseName houseName { get; set; }
		[JsonConverter(typeof(StringEnumConverter))]
		public HouseColor houseColor { get; set; }
		public List<Character> houseMessageCharacters { get; set; }

		
	}
}

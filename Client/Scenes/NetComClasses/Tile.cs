using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{
	public enum messageTileType
	{
		CITY,
		FLAT_SAND,
		DUNE,
		PLATEAU,
		MOUNTAINS,
		HELIPORT
	}

	public class Tile
	{
		[JsonConverter(typeof(StringEnumConverter))]
		public messageTileType MessageTileType { get; set; }
		public int clientID { get; set; }
		public bool hasSpice{ get; set; } 
		public bool isInSandstorm{ get; set; }

		public Tile(messageTileType type)
		{
			
		}

		public override string ToString()
		{
			return MessageTileType.ToString();
		}
	}
}

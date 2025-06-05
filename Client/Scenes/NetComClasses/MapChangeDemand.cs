using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{
	public class MapChangeDemand : AbstractMessage
	{
		[JsonConverter(typeof(StringEnumConverter))]
		public ChangeReason changeReason { get; set; }
		public Coords stormEye { get; set; }
		public List<List<Tile>> newMap { get; set; }

		public MapChangeDemand(ChangeReason changeReason, Coords stormEye, List<List<Tile>> newMap)
		{
			this.changeReason = changeReason;
			this.stormEye = stormEye;
			this.newMap = newMap;
			this.type = MessageType.MAP_CHANGE_DEMAND;
		}

		public override string ToString()
		{
			return $"{nameof(changeReason)}: {changeReason}, {nameof(stormEye)}: {stormEye}";
		}
	}
}

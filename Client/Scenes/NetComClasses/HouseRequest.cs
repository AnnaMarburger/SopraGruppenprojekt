using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{
	public class HouseRequest : AbstractMessage
	{
		[JsonConverter(typeof(StringEnumConverter))]
		public HouseName houseName { get; set; }

		public HouseRequest(HouseName houseName)
		{
			this.houseName = houseName;
			this.type = MessageType.HOUSE_REQUEST;
		}
	}
}

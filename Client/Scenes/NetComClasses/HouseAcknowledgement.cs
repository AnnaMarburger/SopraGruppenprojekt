using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{
	public class HouseAcknowledgement : AbstractMessage
	{
		public int clientID { get; set; }
		[JsonConverter(typeof(StringEnumConverter))]
		public HouseName houseName { get; set; }

		public HouseAcknowledgement(int clientId, HouseName houseName)
		{
			clientID = clientId;
			this.houseName = houseName;
			this.type = MessageType.HOUSE_ACKNOWLEDGEMENT;
		}
	}
}

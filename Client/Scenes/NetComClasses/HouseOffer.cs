using System.Collections.Generic;

namespace Client.Scenes.NetComClasses
{
	public class HouseOffer : AbstractMessage
	{
		public int clientID { get; set; }
		public List<House> houses { get; set; }

		public override string ToString()
		{
			return "HouseOffer\nclientID: " + clientID + "\nhouses: " + houses;
		}

		public HouseOffer(int clientId, List<House> houses)
		{
			clientID = clientId;
			this.houses = houses;
			this.type = MessageType.HOUSE_OFFER;
		}
	}
}

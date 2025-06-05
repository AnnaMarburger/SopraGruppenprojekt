using System.Dynamic;

namespace Client.Scenes.NetComClasses
{
	public class ChangePlayerSpiceDemand : AbstractMessage
	{
		public int clientID { get; set; }
		public int newSpiceValue { get; set; }

		public ChangePlayerSpiceDemand(int clientId, int newSpiceValue)
		{
			clientID = clientId;
			this.newSpiceValue = newSpiceValue;
			this.type = MessageType.CHANGE_PLAYER_SPICE_DEMAND;
		}

		public override string ToString()
		{
			return $"{nameof(clientID)}: {clientID}, {nameof(newSpiceValue)}: {newSpiceValue}";
		}
	}
}

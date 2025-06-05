namespace Client.Scenes.NetComClasses
{
	public class HeliDemand:AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public Coords target;
		public bool crash { get; set; }

		public HeliDemand(Coords target, int clientId, int characterId, bool crash)
		{
			this.target = target;
			clientID = clientId;
			characterID = characterId;
			this.crash = crash;
			this.type = MessageType.HELI_DEMAND;
		}
	}
}

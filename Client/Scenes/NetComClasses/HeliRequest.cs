namespace Client.Scenes.NetComClasses
{
	public class HeliRequest : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public Coords target { get; set; }

		public HeliRequest(int clientId, int characterId, Coords target)
		{
			clientID = clientId;
			characterID = characterId;
			this.target = target;
			this.type = MessageType.HELI_REQUEST;
		}
	}
}

namespace Client.Scenes.NetComClasses
{
	public class GameStateRequest : AbstractMessage
	{
		public int clientID { get; set; }

		public GameStateRequest(int clientId)
		{
			clientID = clientId;
			this.type = MessageType.GAMESTATE_REQUEST;
		}
	}
}

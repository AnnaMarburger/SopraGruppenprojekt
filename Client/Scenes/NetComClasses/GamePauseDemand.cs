namespace Client.Scenes.NetComClasses
{
	public class GamePauseDemand : AbstractMessage
	{
		public int requestedByClientID { get; set; }
		public bool pause { get; set; }

		public GamePauseDemand(int requestedByClientId, bool pause)
		{
			requestedByClientID = requestedByClientId;
			this.pause = pause;
			this.type = MessageType.GAME_PAUSE_DEMAND;
		}
	}
}

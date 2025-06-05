namespace Client.Scenes.NetComClasses
{
	public class PauseRequest : AbstractMessage
	{
		public bool pause { get; set; }

		public PauseRequest(bool pause)
		{
			this.pause = pause;
			this.type = MessageType.PAUSE_REQUEST;
		}
	}
}

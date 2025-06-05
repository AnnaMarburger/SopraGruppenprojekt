namespace Client.Scenes.NetComClasses
{
	public class Rejoin : AbstractMessage
	{
		public string clientSecret { get; set; }

		public Rejoin(string clientSecret)
		{
			this.clientSecret = clientSecret;
			type = MessageType.REJOIN;
		}
	}
}

namespace Client.Scenes.NetComClasses
{
	public class Client
	{
		public int clientID { get; set; }
		public string clientName { get; set; }
		public int x { get; set; }
		public int y { get; set; }

		public Client(string clientName)
		{
			this.clientName = clientName;
		}
	}
}

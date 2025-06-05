namespace Client.Scenes.NetComClasses
{
	public class Join : AbstractMessage
	{
		public string clientName { get; set; }
		public bool isActive { get; set; }
		public bool isCpu { get; set; }

		public Join(string clientName, bool isActive, bool isCpu)
		{
			this.clientName = clientName;
			this.isActive = isActive;
			this.isCpu = isCpu;
			this.type = MessageType.JOIN;
		}
	}
}

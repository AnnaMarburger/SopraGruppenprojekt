namespace Client.Scenes.NetComClasses
{
	public class Error : AbstractMessage
	{
		public int errorCode { get; set; }
		public string errorDescription { get; set; }

		public Error(int errorCode, string errorDescription)
		{
			this.errorCode = errorCode;
			this.errorDescription = errorDescription;
			this.type = MessageType.ERROR;
		}
	}
}

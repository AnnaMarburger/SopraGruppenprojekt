namespace Client.Scenes.NetComClasses
{
	public class MovementDemand : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public SpecsMovement specs { get; set; }

		public MovementDemand(int clientId, int characterId, SpecsMovement specs)
		{
			clientID = clientId;
			characterID = characterId;
			this.specs = specs;
			this.type = MessageType.MOVEMENT_DEMAND;
		}
	}
}

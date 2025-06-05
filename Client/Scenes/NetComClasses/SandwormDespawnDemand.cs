namespace Client.Scenes.NetComClasses
{
	public class SandwormDespawnDemand : AbstractMessage
	{
		public SandwormDespawnDemand()
		{
			this.type = MessageType.SANDWORM_DESPAWN_DEMAND;
		}
	}
}

using Godot;

namespace Client.Scenes.NetComClasses
{
	public class ActionDemand : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public Action action;
		public SpecsAction specs { get; set; }


		protected ActionDemand(Action action, int clientId, int characterId, SpecsAction specs)
		{
			this.action = action;
			clientID = clientId;
			characterID = characterId;
			this.specs = specs;
			this.type = MessageType.ACTION_DEMAND;
		}

		public override string ToString()
		{
			return $"{nameof(action)}: {action}, {nameof(clientID)}: {clientID}, {nameof(characterID)}: {characterID}, {nameof(specs)}: {specs}";
		}
	}
}

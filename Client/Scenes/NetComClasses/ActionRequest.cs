using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Client.Scenes.NetComClasses
{

	public enum Action 
	{
		ATTACK,
		COLLECT,
		KANLY,
		FAMILY_ATOMICS,
		SPICE_HOARDING,
		VOICE,
		SWORDSPIN
	}
	public class ActionRequest : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		[JsonConverter(typeof(StringEnumConverter))]
		public Action action { get; set; }

		public SpecsAction specs { get; set; }

		public ActionRequest(int clientId, int characterId, Action action, SpecsAction specs)
		{
			clientID = clientId;
			characterID = characterId;
			this.action = action;
			this.specs = specs;
			this.type = MessageType.ACTION_REQUEST;
		}

		public override string ToString()
		{
			return $"{nameof(clientID)}: {clientID}, {nameof(characterID)}: {characterID}, {nameof(action)}: {action}, {nameof(specs)}: {specs}";
		}
	}

	
	
}

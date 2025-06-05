namespace Client.Scenes.NetComClasses
{
	public class SpawnCharacterDemand : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public string characterName { get; set; }
		public Coords position { get; set; }
		public Attributes attributes;

		public SpawnCharacterDemand(Attributes attributes, int clientId, int characterId, string characterName, Coords position)
		{
			this.attributes = attributes;
			clientID = clientId;
			characterID = characterId;
			this.characterName = characterName;
			this.position = position;
			this.type = MessageType.SPAWN_CHARACTER_DEMAND;
		}
	}

	public class Attributes
	{
		public CharacterClass characterType { get; set; }
		public float healingMax { get; set; }
		public float healthCurrent { get; set; }
		public float healingHP { get; set; }
		public int MPmax{ get; set; }
		public int MPcurrent{ get; set; }
		public int APmax{ get; set; }
		public int APcurrent{ get; set; }
		public float attackDamage{ get; set; }
		public int inventorySize{ get; set; }
		public int inventoryUsed{ get; set; }
		public bool killedBySandworm{ get; set; }
		public bool isLoud{ get; set; }
	}
}

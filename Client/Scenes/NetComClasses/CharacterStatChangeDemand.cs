namespace Client.Scenes.NetComClasses
{
	public class CharacterStatChangeDemand :AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public Stats stats { get; set; }

		public CharacterStatChangeDemand(int clientId, int characterId, Stats stats)
		{
			clientID = clientId;
			characterID = characterId;
			this.stats = stats;
			this.type = MessageType.CHARACTER_STAT_CHANGE_DEMAND;
		}

		public override string ToString()
		{
			return $"{nameof(clientID)}: {clientID}, {nameof(characterID)}: {characterID}, {nameof(stats)}: {stats}";
		}
	}
	
	
}

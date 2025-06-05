
using System;

namespace Client.Scenes.NetComClasses
{
	public class GameEndMessage : AbstractMessage
	{
		public int winnerID { get; set; }
		public int loserID { get; set; }
		public Object statistics;

		public GameEndMessage(object statistics, int winnerId, int loserId)
		{
			this.statistics = statistics;
			winnerID = winnerId;
			loserID = loserId;
			this.type = MessageType.GAME_END;
		}
	}
}

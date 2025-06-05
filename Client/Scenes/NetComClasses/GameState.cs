using System.Collections.Generic;

namespace Client.Scenes.NetComClasses
{
	public class Gamestate : AbstractMessage
	{
		public int clientID { get; set; }
		public List<int> activelyPlayingIDs { get; set; }
		public List<string> history { get; set; }

		public Gamestate(int clientId = default, List<int> activelyPlayingIDs = null, List<string> history = null)
		{
			clientID = clientId;
			this.activelyPlayingIDs = activelyPlayingIDs;
			this.history = history;
			this.type = MessageType.GAMESTATE;
		}

		public override string ToString()
		{
			return $"{nameof(clientID)}: {clientID}, {nameof(activelyPlayingIDs)}: {activelyPlayingIDs}, {nameof(history)}: {history}";
		}
	}
}

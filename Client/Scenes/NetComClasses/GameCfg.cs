using System.Collections.Generic;

namespace Client.Scenes.NetComClasses
{
	public class GameCfg : AbstractMessage
	{
		public Scenario scenario { get; set; }
		public Root party { get; set; }
		public List<Client> citiesToClients { get; set; }
		public StormEye stormEye { get; set; }

		public GameCfg(Scenario scenario, Root party, List<Client> citiesToClients, StormEye stormEye)
		{
			this.scenario = scenario;
			this.party = party;
			this.citiesToClients = citiesToClients;
			this.stormEye = stormEye;
			this.type = MessageType.GAMECFG;
		}

		public override string ToString()
		{

			string msg = "{\n";

			if(citiesToClients != null)
			{
				foreach (var item in citiesToClients)
				{
					msg += "\n{ID: " +item.clientID + ", Name: " + item.clientName +"}";
				}
			}
			msg += "\n}";
			return msg;
		}

	}

	public class Scenario
	{
		public List<List<string>> scenario { get; set; }
	}
}

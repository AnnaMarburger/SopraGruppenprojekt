using System;
using System.Collections.Generic;
using Godot;

namespace Client.Scenes.NetComClasses
{
	public class MovementRequest : AbstractMessage
	{
		public int clientID { get; set; }
		public int characterID { get; set; }
		public SpecsMovement specs { get; set; }

		public MovementRequest(int clientId, int characterId, SpecsMovement specsMovement)
		{
			clientID = clientId;
			characterID = characterId;
			specs = specsMovement;
			this.type = MessageType.MOVEMENT_REQUEST;
		}
	}

	
}


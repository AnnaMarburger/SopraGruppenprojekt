namespace Client.Scenes.NetComClasses
{
    public class TurnDemand : AbstractMessage
    {
        public int clientID { get; set; }
        public int characterID { get; set; }

        public TurnDemand(int clientId, int characterId)
        {
            clientID = clientId;
            characterID = characterId;
            this.type = MessageType.TURN_DEMAND;
        }
    }
}
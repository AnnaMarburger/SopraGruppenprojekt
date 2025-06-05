namespace Client.Scenes.NetComClasses
{
    public class EndTurnRequest : AbstractMessage
    {
        public int clientID { get; set; }
        public int characterID { get; set; }

        public EndTurnRequest(int clientId, int characterId)
        {
            clientID = clientId;
            characterID = characterId;
            this.type = MessageType.END_TURN_REQUEST;
        }
    }
}
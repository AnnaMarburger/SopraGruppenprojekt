namespace Client.Scenes.NetComClasses
{
    public class TransferDemand : AbstractMessage
    {
        public int clientID { get; set; }
        public int characterID { get; set; }
        public int targetID { get; set; }

        public TransferDemand(int clientId, int characterId, int targetId)
        {
            clientID = clientId;
            characterID = characterId;
            targetID = targetId;
            this.type = MessageType.TRANSFER_DEMAND;
        }
    }
}
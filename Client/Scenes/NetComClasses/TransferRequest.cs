namespace Client.Scenes.NetComClasses
{
    public class TransferRequest : AbstractMessage
    {
        public int clientID { get; set; }

        public int characterID { get; set; }
        public int targetID { get; set; }
        public int amount { get; set; }

        public TransferRequest(int clientId, int characterId, int targetId, int amount)
        {
            clientID = clientId;
            characterID = characterId;
            targetID = targetId;
            this.amount = amount;
            this.type = MessageType.TRANSFER_REQUEST;
        }
    }
}
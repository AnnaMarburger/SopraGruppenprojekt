namespace Client.Scenes.NetComClasses
{
    public class UnpauseGameOffer : AbstractMessage
    {
        public int requestedByClientID { get; set; }

        public UnpauseGameOffer(int requestedByClientId)
        {
            requestedByClientID = requestedByClientId;
            this.type = MessageType.UNPAUSE_GAME_OFFER;
        }
    }
}
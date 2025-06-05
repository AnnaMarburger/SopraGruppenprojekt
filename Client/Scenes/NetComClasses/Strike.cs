namespace Client.Scenes.NetComClasses
{
    public class Strike : AbstractMessage
    {
        public int clientID { get; set; }
        public string wrongMessage { get; set; }
        public int count { get; set; }

        public Strike(int clientId, string wrongMessage, int count)
        {
            clientID = clientId;
            this.wrongMessage = wrongMessage;
            this.count = count;
            this.type = MessageType.STRIKE;
        }
    }
}
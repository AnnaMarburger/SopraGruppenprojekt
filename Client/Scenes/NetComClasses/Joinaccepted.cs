
namespace Client.Scenes.NetComClasses
{
    public class JoinAccepted : AbstractMessage
    {
        public string clientSecret { get; set; }
        public int clientID { get; set; }

        public override string ToString()
        {
            return "JoinAccepted\nsecret: " + clientSecret + "\nclientID: " + clientID;
        }

        public JoinAccepted(string clientSecret, int clientId)
        {
            this.clientSecret = clientSecret;
            clientID = clientId;
            this.type = MessageType.JOINACCEPTED;
        }
    }
}
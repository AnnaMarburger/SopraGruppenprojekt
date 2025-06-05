namespace Client.Scenes.NetComClasses
{
    public class AtomicsUpdateDemand : AbstractMessage
    {
        public int clientID { get; set; }
        public bool shunned { get; set; }
        public int atomicsLeft { get; set; }

        public AtomicsUpdateDemand(int clientId, bool shunned, int atomicsLeft)
        {
            clientID = clientId;
            this.shunned = shunned;
            this.atomicsLeft = atomicsLeft;
            this.type = MessageType.ATOMICS_UPDATE_DEMAND;
        }

        public override string ToString()
        {
            return $"{nameof(clientID)}: {clientID}, {nameof(shunned)}: {shunned}, {nameof(atomicsLeft)}: {atomicsLeft}";
        }
    }
}
namespace Client.Scenes.NetComClasses
{
    public class SandwormSpawnDemand : AbstractMessage
    {
        public int clientID { get; set; }
        public int characterID { get; set; }
        public Coords position { get; set; }

        public SandwormSpawnDemand(int clientId, int characterId, Coords position)
        {
            clientID = clientId;
            characterID = characterId;
            this.position = position;
            this.type = MessageType.SANDWORM_SPAWN_DEMAND;
        }
    }
}
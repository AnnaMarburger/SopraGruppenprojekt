using System.Collections.Generic;
using System.IO;

namespace Client.Scenes.NetComClasses
{
    public class SandwormMoveDemand : AbstractMessage{
        public List<Coords> path;

        public SandwormMoveDemand(List<Coords> path)
        {
            this.path = path;
            this.type = MessageType.SANDWORM_MOVE_DEMAND;
        }
    }
}
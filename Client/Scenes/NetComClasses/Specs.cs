using System.Globalization;
using System.Collections.Generic;

namespace Client.Scenes.NetComClasses
{
    public class SpecsMovement
    {
        public List<Coords> path { get; set; }

        public SpecsMovement(List<Coords> path)
        {
            this.path=path;
        }
    }
    
    public class SpecsAction
    {
        public Coords target{ get; set; }

        public SpecsAction(Coords target)
        {
            this.target = target;
        }
    }
}
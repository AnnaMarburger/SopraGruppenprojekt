using System.Collections.Generic;

namespace Client.Scenes.NetComClasses
{
    public class Stats
    {
        public Stats(float hp, int ap, int mp, int spice, bool isLoud, bool isSwallowed)
        {
            HP = hp;
            AP = ap;
            MP = mp;
            this.spice = spice;
            this.isLoud = isLoud;
            this.isSwallowed = isSwallowed;
        }

        public float HP { get; set; }
        public int AP{ get; set; }
        public int MP { get; set; }
        public int spice { get; set; }
        public bool isLoud { get; set; }
        public bool isSwallowed { get; set; }
    }
}
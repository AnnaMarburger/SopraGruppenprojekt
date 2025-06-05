namespace Client.Scenes.NetComClasses
{
	public class Coords
	{
		public int x { get; set; }
		public int y { get; set;}

		public Coords(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public override string ToString()
		{
			return x + " " + y;
		}

		public bool Equals(Coords obj)
		{
			return (obj.x == x && obj.y == y);
		}
	}
}

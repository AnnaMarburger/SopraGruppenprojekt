using Godot;

namespace Editor.Scenes
{
	public class Tile2 : Button
	{

		// Called when the node enters the scene tree for the first time.
		public override void _Ready()
		{
		
		}


		public override void _Process(float delta)
		{
	 
		}

		public override void _Pressed()
		{
			GetParent().GetParent().GetNode<Map>("Map").SetCurrentTile(1);
		}
	}
}

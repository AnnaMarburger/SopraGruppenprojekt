using Godot;

namespace Editor.Scenes
{
	public class Tile6 : Button
	{
		// Declare member variables here. Examples:
		// private int a = 2;
		// private string b = "text";

		// Called when the node enters the scene tree for the first time.
		public override void _Ready()
		{
			
		}

		public override void _Pressed()
		{
			GetParent().GetParent().GetNode<Map>("Map").SetCurrentTile(5);
			GD.Print(GetParent().GetParent().GetNode<Map>("Map").GetCurrentTile());
		}
	}
}

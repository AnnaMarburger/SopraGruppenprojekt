using Godot;

namespace Editor.Scenes
{
	public class Tile1 : Button
	{

		public override void _Ready()
		{
		}

		public override void _Process(float delta)
		{
	  
		}

		public override void _Pressed()
		{
			GetParent().GetParent().GetNode<Map>("Map").SetCurrentTile(2);
		   
		}
	}
}

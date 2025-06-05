using System;
using Godot;

namespace Editor.Scenes
{
	
	/**
	 * @description: This prepares the scene for editing the scenario.
	 */
	public class RootNode : Godot.Node2D
	{

		// Called when the node enters the scene tree for the first time.
		private Map _tm;
		private MainCamera _cam;
		

		public override void _Ready()
		{
			//Console.Write(1);
			_tm = GetNode<Map>("Map");
			_cam = GetNode<MainCamera>("MainCamera");
			_SetCameraLimit();
		}

		public override void _UnhandledInput(InputEvent @event)
		{
			// Mouse in viewport coordinates.
			if (@event is InputEventMouseButton eventMouseButton)
			{
				if (@event.IsActionPressed("left"))
				{
				_tm.Set(_tm.WorldToMap(GetGlobalMousePosition()),_tm.GetCurrentTile());
				}
			}
		}


		private void _SetCameraLimit()
		{
			var boundaries=_tm.GetUsedRect();
			var cellSize = _tm.CellSize;
			_cam.LimitLeft = (int)Math.Round(boundaries.Position.x * cellSize.x);
			_cam.LimitRight = (int)Math.Round(boundaries.End.x * cellSize.x);
			_cam.LimitTop = (int)Math.Round(boundaries.Position.y * cellSize.y);
			_cam.LimitBottom = (int)Math.Round(boundaries.End.y * cellSize.y);
			
			
	   

		}
	
	}
}

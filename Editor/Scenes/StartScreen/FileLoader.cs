using System;
using Godot;

namespace Editor.Scenes.StartScreen
{
	public class FileLoader : FileDialog
	{
		
		// Called when the node enters the scene tree for the first time.
		public override void _Ready()
		{
			String[] filters = new string[1];
			filters[0]="*.Json;JSON FILES";
			this.Filters=filters;
		}

		public void _on_existingFileButton_pressed()
		{
			this.Popup_();
		}


	}
}

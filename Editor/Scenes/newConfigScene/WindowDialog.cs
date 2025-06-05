using System;
using Editor.Scenes.AutoLoads;
using Editor.Scenes.StartScreen;
using Godot;

namespace Editor.Scenes.newConfigScene
{
	public class WindowDialog : Godot.WindowDialog
	{

		// Called when the node enters the scene tree for the first time.
		public override void _Ready()
		{
		}

		//Called when OK Button is pressed
		public void _on_OKButton_pressed()
		{
			try
			{
				GetNode<PermaScene>("/root/PermaScene").addScenario(null);
				//get inserted width and height from user
				int widthTmp = GetNode<Width>("Width").Text.ToInt();
				int heightTmp = GetNode<LineEdit>("Height").Text.ToInt();
				//check if scenario size is at least 4 tiles
				if (widthTmp * heightTmp < 4 || widthTmp <0)
				{
					throw (new Exception("Minimal size is 4 or entered a negative number"));
				}
				
				
				//save data in permastore
				WHPerma wh = GetNode<WHPerma>("/root/WHPerma");
				wh.setWH(widthTmp, heightTmp);

				//change screen
				GetTree().ChangeScene("res://Scenes/newConfigScene/newEditorScreen.tscn");
			}
			catch (Exception e)
			{
				GD.Print(e);
			}
		
		}

		//open pop-up when new Config Button from start screen is pressed
		public void _on_newConfigButton_pressed()
		{
			this.Popup_();
		}
	}
}

using Godot;
using System;

public class GameOverDialog : AcceptDialog
{
    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
    }

    public void PopUp(Boolean won)
    {
        Texture tex;
        if(won)
        {
            tex = (Texture)GD.Load("res://Assets/won.png");
            this.WindowTitle = "You Won!";
			
        } else
        {
            tex = (Texture)GD.Load("res://Assets/lost.png");
            this.WindowTitle = "You Lost!";
        }
        GetNode<Godot.Sprite>("sprite").Texture = tex;
        this.Show();
    }

}

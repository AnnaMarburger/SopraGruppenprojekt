using Godot;
using System;

public partial class music : AudioStreamPlayer
{
    // Declare member variables here. Examples:
    // private int a = 2;
    // private string b = "text";

    // Called when the node enters the scene tree for the first time.
    

    public void _on_togglemusic_pressed()
    {
        if (this.Playing)
        {
            this.Stop();
        }
        else
        {
            this.Play();
        }
    }
}

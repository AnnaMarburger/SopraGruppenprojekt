namespace Editor.Scenes
{
    public class HSlider : Godot.HSlider
    {
        // Declare member variables here. Examples:
        // private int a = 2;
        // private string b = "text";

        // Called when the node enters the scene tree for the first time.
        public override void _Ready()
        {
            MinValue = 10;
            MaxValue = 100;
            TickCount = 10;
            
        }


        
    }
}


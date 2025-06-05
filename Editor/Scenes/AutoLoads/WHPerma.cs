using Godot;

namespace Editor.Scenes.AutoLoads
{
    /**
     * @description: Saves the Width and Height entered at the start screen.
     */
    public class WHPerma : Node
    {
        private int height = 5;
        private int width = 10;

        // Called when the node enters the scene tree for the first time.
        public override void _Ready()
        {
        
        }

        public void setWH(int width, int heigth)
        {
            this.width = width;
            this.height = heigth;
        }

        public int getHeight()
        {
            return height;
        }

        public int getWidth()
        {
            return width;
        }

    }
}

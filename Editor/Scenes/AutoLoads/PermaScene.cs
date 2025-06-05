using Godot;

namespace Editor.Scenes.AutoLoads
{
    /**
     * @description: Saves the Scenario at all times. A default is loaded in case of custom size
     */
    public class PermaScene : Node
    {
        private int[,] scenario;

        public void addScenario(int[,] ids)
        {
            scenario = ids;
        }

        public int[,] getScenario()
        {
            return scenario;
        }

        public override void _Ready()
        {
            //GD.Print(this.GetPath());
        }

        
    }
}
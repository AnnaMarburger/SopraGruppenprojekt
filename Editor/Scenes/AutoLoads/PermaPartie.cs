using System;
using Editor.Scenes.StartScreen;
using Godot;

namespace Editor.Scenes.AutoLoads
{
    
    /**
     * @description: Saves the Partie-Config at all times. A default set is loaded at the start.
     */
    public class PermaPartie : Node
    {
        private Root _partie;
        
        // Called when the node enters the scene tree for the first time.
        public override void _Ready()
        {
        
        }

        public void setPartie(Root partie)
        {
            _partie = partie;
        }

        public Root getPartie()
        {
            return _partie;
        }

        public Noble getNoble()
        {
            if (_partie == null)
                throw new Exception("Noble Not Found");
            return _partie.noble;
        }
        
        public Fighter getFighter()
        {
            if (_partie == null)
                throw new Exception("Noble Not Found");
            return _partie.fighter;
        }
        
        public Mentat getMentat()
        {
            if (_partie == null)
                throw new Exception("Noble Not Found");
            return _partie.mentat;
        }
        
        public BeneGesserit getBeneGesserit()
        {
            if (_partie == null)
                throw new Exception("Noble Not Found");
            return _partie.beneGesserit;
        }

        public void printValues()
        {
            GD.Print(_partie.ToString());
        }
    }
}

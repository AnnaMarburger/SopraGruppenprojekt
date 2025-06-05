using Godot;

namespace Editor.Scenes.StartScreen
{
    
    /**
     * @description: This is the backend for the parsed json Partie config
     */
    public class BeneGesserit
    {
        public float maxHP { get; set; }
        public int maxMP { get; set; }
        public int maxAP { get; set; }
        public float damage { get; set; }
        public int inventorySize { get; set; }
        public float healingHP { get; set; }
        
        public override string ToString()
        {
            string output = "beneGesserit\n";
            output += maxAP + "\n";
            output += maxHP + "\n";
            output += maxMP + "\n";
            output += damage+ "\n";
            output += inventorySize + "\n";
            output += healingHP + "\n";
            return output;
        }
    }

    public class Fighter
    {
        public float maxHP { get; set; }
        public int maxMP { get; set; }
        public int maxAP { get; set; }
        public float damage { get; set; }
        public int inventorySize { get; set; }
        public float healingHP { get; set; }
        public override string ToString()
        {
            string output = "fighter\n";
            output += maxAP + "\n";
            output += maxHP + "\n";
            output += maxMP + "\n";
            output += damage+ "\n";
            output += inventorySize + "\n";
            output += healingHP + "\n";
            return output;
        }
        
    }

    public class Mentat
    {
        public float maxHP { get; set; }
        public int maxMP { get; set; }
        public int maxAP { get; set; }
        public float damage { get; set; }
        public int inventorySize { get; set; }
        public float healingHP { get; set; }
        
        public override string ToString()
        {
            string output = "mentat\n";
            output += maxAP + "\n";
            output += maxHP + "\n";
            output += maxMP + "\n";
            output += damage+ "\n";
            output += inventorySize + "\n";
            output += healingHP + "\n";
            return output;
        }
    }

    public class Noble
    {
        public float maxHP { get; set; }
        public int maxMP { get; set; }
        public int maxAP { get; set; }
        public float damage { get; set; }
        public int inventorySize { get; set; }
        public float healingHP { get; set; }

        public override string ToString()
        {
            string output = "noble\n";
            output += maxAP + "\n";
            output += maxHP + "\n";
            output += maxMP + "\n";
            output += damage+ "\n";
            output += inventorySize + "\n";
            output += healingHP + "\n";
            return output;
        }
    }

    public class Root
    {
        public Noble noble { get; set; }
        public Mentat mentat { get; set; }
        public BeneGesserit beneGesserit { get; set; }
        public Fighter fighter { get; set; }
        public int numbOfRounds { get; set; }
        public double actionTimeUserClient { get; set; }
        public double actionTimeAiClient { get; set; }
        public double highGroundBonusRatio { get; set; }
        public double lowerGroundMalusRatio { get; set; }
        public double kanlySuccessProbability { get; set; }
        public int spiceMinimum { get; set; }
        public string cellularAutomaton { get; set; }
        public int sandWormSpeed { get; set; }
        public int sandWormSpawnDistance { get; set; }
        
        public int maxStrikes { get; set; }
        public double cloneProbability { get; set; }
        public int minPauseTime { get; set; }
        
        public double crashProbability { get; set; }

        public override string ToString()
        {
            string output = "";
            output += numbOfRounds + "\n";
            output += highGroundBonusRatio + "\n";
            output += lowerGroundMalusRatio + "\n";
            output += actionTimeUserClient + "\n";
            output += actionTimeAiClient + "\n";
            output += kanlySuccessProbability + "\n";
            output += spiceMinimum + "\n";
            output += cellularAutomaton + "\n";
            output += sandWormSpeed + "\n";
            output += sandWormSpawnDistance + "\n";
            output += cloneProbability + "\n";
            output += minPauseTime + "\n";
            output += maxStrikes + "\n";
            output += crashProbability+"\n";
            output += noble.ToString();
            output += beneGesserit.ToString();
            output += mentat.ToString();
            output += fighter.ToString();
            
            return output;
        }
    }
}
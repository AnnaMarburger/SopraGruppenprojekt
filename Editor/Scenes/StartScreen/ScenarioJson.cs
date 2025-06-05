using System.Collections.Generic;
using Godot;

namespace Editor.Scenes.StartScreen
{
    
    /**
     * @descpription: This is the backend for the scenarion json. Also validates the input and converts it to int ids
     */
    public class SzenarioRoot
    {
        public List<List<string>> scenario { get; set; }

        public bool valid()
        {

            int sizeX = scenario.Count;
            int sizeY = scenario[0].Count;
            int cityCount = 0;
            for (int x = 0; x < sizeX; x++)
            {

                if (scenario[x].Count != sizeY)
                {
                    GD.PrintErr("column " +x+" not as big as 1st one");
                    return false;
                }
                   
                for (int y = 0; y < sizeY; y++)
                {
                    if (scenario[x][y].Equals("CITY"))
                        cityCount++;
                }
                
            }

            if (cityCount != 2)
            {
                GD.PrintErr("not two cities");
                return false;
            }

            if (sizeX*sizeY<4)
            {
                GD.PrintErr("Map not big enough, at least 4 fields");
            }
            
            return true;
            
        }

        public int[,] toIds()
        {
            if (valid())
            {
                
                int sizeX = scenario.Count;
                int sizeY = scenario[0].Count;
                int[,] ids = new int[sizeY, sizeX];
                {
                    for (int x = 0; x < sizeX; x++)
                    {
                        for (int y = 0; y < sizeY; y++)
                        {
                            if (scenario[x][y].Equals("CITY"))
                            {
                                ids[y, x] = 0;  
                            }
                            if (scenario[x][y].Equals("DUNE"))
                            {
                                ids[y, x] = 1;
                            }
                            if (scenario[x][y].Equals("FLAT_SAND"))
                            {
                                ids[y, x] = 2;
                            }
                            if (scenario[x][y].Equals("MOUNTAINS"))
                            {
                                ids[y, x] = 3;
                            }
                            if (scenario[x][y].Equals("PLATEAU"))
                            {
                                ids[y, x] = 4;
                            }
                            if (scenario[x][y].Equals("HELIPORT"))
                            {
                                ids[y, x] = 5;
                                
                            }
                            
                            
                            
                            
                        }
                    }
                }
                
                return ids;
            }

            return null;
        }
            
    }
}

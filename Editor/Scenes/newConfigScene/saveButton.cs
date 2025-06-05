using Godot;
using System;
using System.Collections.Generic;
using System.IO;
using Editor.Scenes;
using Editor.Scenes.AutoLoads;
using Editor.Scenes.StartScreen;
using Newtonsoft.Json;

public class saveButton : TextureButton
{
    private PermaScene _ps;
    private int[,] _scenario;
    private WHPerma _wh;
    private  List<List<string>> _convertedMap;
    private SzenarioRoot _sr;
    

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        //loading tiles from permascene, width and height from whperma and setting up new scenarioroot
        _ps = GetNode<PermaScene>("/root/PermaScene");
        _sr = new SzenarioRoot();
        _wh = GetNode<WHPerma>("/root/WHPerma");
    }

    public void _on_selectPathDialog_file_selected(string path)
    {
        //loading cuurent tiles into permascene
        _ps = GetNode<PermaScene>("/root/PermaScene");
        _scenario = _ps.getScenario();

        convertMap();
        //setting Stringlist into scenarioroot
        _sr.scenario = _convertedMap;
        _save_Map(path);
    }

    public void _save_Map(string path)
    {
        //Replace 'res:/' from given path with a single dot
        string[] pathArray = path.Split("res:/");
        string pathWithoutRes = "." + pathArray[1];

        //Write the given object into the json file
        JsonSerializer jsonSerializer = new JsonSerializer();
        StreamWriter sw = new StreamWriter(pathWithoutRes);
        JsonWriter jsonWriter = new JsonTextWriter(sw);
			
        jsonSerializer.Serialize(jsonWriter, _sr);
			
        jsonWriter.Close();
        sw.Close();
    }
    
    /**
     * Converts the map visualization of 2D-int-Array into Stringlist
     */
    public void convertMap()
    {

        _convertedMap = new List<List<string>>();
        
        
        for (int j = 0; j < _scenario.GetLength(1); j++)
        {
            _convertedMap.Add(new List<string>());
            for (int i = 0; i < _scenario.GetLength(0); i++)
            {
                
                if (_scenario[i,j]==0)
                {
                    _convertedMap[j].Add("CITY");          
                }
                else if (_scenario[i,j]==1)
                {
                    _convertedMap[j].Add( "DUNE");
                }
                else if (_scenario[i,j]==2)
                {
                    _convertedMap[j].Add("FLAT_SAND");
                }
                else if (_scenario[i,j]==3)
                {
                    _convertedMap[j].Add("MOUNTAINS");
                }
                else if (_scenario[i,j]==4)
                {
                    _convertedMap[j].Add("PLATEAU");
                }	
                else if (_scenario[i,j]==5)
                {
                    _convertedMap[j].Add("HELIPORT");
                }	
            }
        }
        
        
    }
    
    
    /**
     * for debugging
     */
    public void printMap()
    {
        for (int i = 0; i < _scenario.GetLength(0); i++)
        {
            for (int j = 0; j < _scenario.GetLength(1); j++)
            {
					
                Console.Write(_convertedMap[i][j]);
					
            }
            Console.WriteLine();
        }
			
    }
}

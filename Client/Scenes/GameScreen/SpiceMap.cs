using Godot;
using System;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Client.Scenes.Autoloads;

public class SpiceMap : TileMap
{
    private GameState _gs;
    Tile[,] scenario;
    int width;
    int height;

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
    }


    //Sets Spice 
    public void SetSpiceOnTiles()
    {
        Clear();
        _gs = GetNode<GameState>("/root/GameState");
        scenario = _gs.getScenario();
        height = scenario.GetLength(0);
        width = scenario.GetLength(1);

        for(int i=0; i<height; i++)
        {
            for(int j=0; j<width; j++)
            {
                Tile tile = scenario[i,j];
                if(tile.hasSpice)
                {
                    SetCell(j, i, 0);
                }

            }
        }

    }

}

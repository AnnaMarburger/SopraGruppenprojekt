using Godot;
using System;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using System.Collections.Generic;

public class SandstormMap : Godot.TileMap
{
    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
    }


    public void updateSandstorm(Coords newMid, int maxX, int maxY)
    {
        this.Clear();
        List<Coords> neighbours = new List<Coords>();
        neighbours.Add(new Coords(newMid.y, newMid.x));
        neighbours.Add(new Coords(newMid.y-1, newMid.x-1));
        neighbours.Add(new Coords(newMid.y-1, newMid.x));
        neighbours.Add(new Coords(newMid.y, newMid.x-1));
        neighbours.Add(new Coords(newMid.y-1, newMid.x+1));
        neighbours.Add(new Coords(newMid.y+1, newMid.x-1));
        neighbours.Add(new Coords(newMid.y+1, newMid.x));
        neighbours.Add(new Coords(newMid.y, newMid.x+1));
        neighbours.Add(new Coords(newMid.y+1, newMid.x+1));

        GD.Print(newMid.ToString());

        foreach (Coords field in neighbours)
        {
            if(!(field.x>= maxX || field.x< 0 || field.y >= maxY || field.y <0))
        {
             SetCell(field.x, field.y, 0);
        }
        }
        

    }
}

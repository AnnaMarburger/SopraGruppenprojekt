using Godot;
using System;
using Client.Scenes.NetComClasses;

public class HighlightMap : TileMap
{

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        
    }

    public void SetHighlight(Coords pos)
    {
        this.Clear();
        SetCell(pos.x, pos.y, 0);
    }
}

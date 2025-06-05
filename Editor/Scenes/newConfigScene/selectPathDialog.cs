using Godot;
using System;
using Editor.Scenes;
using Newtonsoft.Json.Serialization;

public class selectPathDialog : FileDialog
{
    

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        String[] filters = new string[1];
        filters[0]="*.json;JSON FILES";
        this.Filters=filters;
    }

    public void _on_saveButton_pressed()
    {
        Map m = GetNode<Map>("/root/RootNode/Map");
        if(m.getCityCount()!=2)
            GD.PushError("Not 2 cities");
        else if(!(m.getHeliCount() ==0 || m.getHeliCount() >1))
            GD.PushError("Heliports invalid");
        else
            this.Popup_();
    }
    

}
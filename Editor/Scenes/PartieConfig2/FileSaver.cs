using Godot;
using System;

public class FileSaver : FileDialog
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
        this.Popup_();
    }
    

}

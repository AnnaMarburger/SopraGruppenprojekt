using Godot;
using System;
using System.Collections.Generic;
using System.Text;
using Client.Scenes;
using Client.Scenes.Autoloads;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Editor.Scenes;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Linq;

public class PauseScreen : Node2D, Observable
{
    private ServerConnection _sc;

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        _sc = GetNode<ServerConnection>("/root/ServerConnection");
        _sc.setCurrentHandler(this);
        if(_sc.getpauseclientID() != _sc.getOwnID())
        {
            GetNode<Button>("Button").SetDisabled(true);
        }
    }

    public void _on_Button_pressed()
    {
        _sc.pauseRequest(false);
    }

    public void parseMessage(string data)
    {
        JObject jInput = JObject.Parse(data);
        string messageType = jInput.GetValue("type").ToString();

        switch(messageType)
        {
            case "UNPAUSE_GAME_OFFER":
                GD.Print("You can go back to game now");
                _unpauseGameOffer(data);
                break;
             case "GAME_PAUSE_DEMAND":
                _gamePauseDemand(data);
                break;
        }
    }

    public void _unpauseGameOffer(String data)
    {
        UnpauseGameOffer msg = JsonConvert.DeserializeObject<UnpauseGameOffer>(data);
        GD.Print("You can go back to game now");
        GetNode<Button>("Button").SetDisabled(false);
    }

     public void _gamePauseDemand(String data)
    {
        GD.Print("Return to game");
        GamePauseDemand msg = JsonConvert.DeserializeObject<GamePauseDemand>(data);
        GD.Print(msg.pause);
        if(msg.pause == false)
        {
            GetTree().ChangeScene("res://Scenes/GameScreen/GameScreen.tscn");
        }
    }

}

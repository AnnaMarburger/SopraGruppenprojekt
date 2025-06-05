using System.Runtime.CompilerServices;
using System.Globalization;
using Client.Scenes.NetComClasses;
using Godot;
using System;
using System.Timers;
using System.Collections.Generic;
using Client.Scenes.GameClasses;
using Character = Client.Scenes.GameClasses.Character;
using System.Threading;
using Thread = System.Threading.Thread;
using Timer = System.Timers.Timer;

public class SandwormMap : TileMap
{
    public bool active;
    Queue<Coords> pathQ;
    private CharacterMap _cm;
    private GameState _gs;
    private Timer timer;
    private Client.Scenes.GameClasses.Character target;

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        pathQ = new Queue<Coords>();
       
        _gs = GetNode<GameState>("/root/GameState");
    }

    public void placeSandworm(Coords pos)
    {
        this.Clear();
        SetCell(pos.x, pos.y, 0);
    }

    private void moveToNextField()
    {
        GD.Print("I'm moving");
        if(pathQ.Count != 0)
        {
            target._stats.HP = 1;
            
            Coords next = pathQ.Dequeue();
            placeSandworm(next);
        }else
        {
            timer.Stop();
            target._stats.HP = 0;
         
            
        }
    }

    public void moveSandworm(List<Coords> path, int target)
    {
        this.target = _gs.getCharacterById(target);
        this.target._stats.HP = 1;
        
        timer = new System.Timers.Timer() ;
        timer.Elapsed += new ElapsedEventHandler(DisplayTimeEvent);
        timer.Interval = 3000;
        timer.Start() ;

        
        // put coords list in a queue
        foreach(Coords coord in path)
        {
            pathQ.Enqueue(coord);
           
        }
    }
    
    public void DisplayTimeEvent(object source, ElapsedEventArgs e)
    {
        moveToNextField();
    }



    public void despawn()
    {
        this.Clear();
        active = false;
    }

}

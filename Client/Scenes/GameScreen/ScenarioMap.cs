using System.Security.AccessControl;
using System.Net;
using System.IO;
using System.Diagnostics;
using System.Collections.Generic;
//using System.Xml.Xsl.Runtime;
using Godot;
using System;
using Client.Scenes.GameClasses;
using Client.Scenes.NetComClasses;
using Client.Scenes.Autoloads;

public class ScenarioMap : TileMap
{
	private GameState _gs;
	public Tile[,] scenario;
	public int width;
	public int height;

	// Called when the node enters the scene tree for the first time.
	public override void _Ready()
	{
	}

	//Sets the Tiles of the Scenario in GameScreen
	public void SetTiles()
	{
		_gs = GetNode<GameState>("/root/GameState");
		scenario = _gs.getScenario();
		height = scenario.GetLength(0);
		width = scenario.GetLength(1);

		for(int i=0; i<height; i++)
		{
			for(int j=0; j<width; j++)
			{
				Tile tile = scenario[i,j];
				messageTileType type = tile.MessageTileType;
				int typeInt = 0;
				switch(type){
					case(messageTileType.CITY):
						typeInt = 0;
						break;
					case(messageTileType.DUNE):
						typeInt = 1;
						break;
					case(messageTileType.FLAT_SAND):
						typeInt = 2;
						break;
					case(messageTileType.MOUNTAINS):
						typeInt = 3;
						break;
					case(messageTileType.PLATEAU):
						typeInt = 4;
						break;
					case(messageTileType.HELIPORT):
						typeInt = 5;
						break;
				}

				SetCell(j, i, typeInt);
				
			}
		}

	}

	public List<Coords> getPath(Coords end, Coords start, int MP)
	{
		end = new Coords(end.y,end.x);
		start=new Coords(start.y,start.x);
		//check if end is valid field
		if(scenario[end.x, end.y].MessageTileType ==  messageTileType.MOUNTAINS || scenario[end.x, end.y].MessageTileType ==  messageTileType.CITY)
		{
			return null;
		}

		if (end.x >= scenario.GetLength(0) || end.x < 0 || end.y >= scenario.GetLength(1) || end.y < 0)
			return null;

		QItem source = new QItem(start.x, start.y, 0, null);
		List<Coords> currentPath = new List<Coords>();

		// applying BFS on matrix cells starting from source
		Queue<QItem> queue = new Queue<QItem>();
		queue.Enqueue(new QItem(source.row, source.col, 0, source.prev));
		bool[,] visited = new bool[scenario.GetLength(0),scenario.GetLength(1)];
		visited[source.row,source.col] = true;
		GD.Print("End:"+end.x + " "+ end.y);

		while (queue.Count != 0) {
			QItem p = queue.Dequeue();
			
			// Destination found;
			
			if (p.row == end.x & p.col == end.y)
			{
				//save coords of path in list
				QItem current = p;
				while(current.prev != null) {
					currentPath.Add(new Coords(current.col, current.row));
					current = current.prev;
				}

				//check if character has enough movement points for chosen path
				if(currentPath.Count > MP)
				{
					GD.Print("not enough mvement points");
					return null;
				}
				else
					return currentPath;

			}
		
			// moving up
			if (isValid(p.row - 1, p.col, scenario, visited)) {
				queue.Enqueue(new QItem(p.row - 1, p.col,p.dist + 1, p));
				visited[p.row - 1,p.col] = true;
			}
		
			// moving down
			if (isValid(p.row + 1, p.col, scenario, visited)) {
				queue.Enqueue(new QItem(p.row + 1, p.col, p.dist + 1, p));
				visited[p.row + 1,p.col] = true;
			}
		
			// moving left
			if (isValid(p.row, p.col - 1, scenario, visited)) {
				queue.Enqueue(new QItem(p.row, p.col - 1, p.dist + 1, p));
				visited[p.row,p.col - 1] = true;
			}
		
			// moving right
			if (isValid(p.row, p.col + 1, scenario, visited)) {
				queue.Enqueue(new QItem(p.row, p.col + 1, p.dist + 1, p));
				visited[p.row,p.col + 1] = true;
			}
		}

		//couldn't find path

		return null;
	}

	//check if field is valid
	private bool isValid(int x, int y, Tile[,] grid, bool[,] visited){
		if (x >= 0 && y >= 0 && (x < grid.GetLength(0)) && (y < grid.GetLength(1))
			&&((grid[x,y].MessageTileType !=  messageTileType.MOUNTAINS || grid[x,y].MessageTileType != messageTileType.CITY))
			&& visited[x,y] == false) {
				GD.Print(x +" "+y+" " +grid[x,y].MessageTileType.ToString());
			return true;
		}
		return false;
		
	}

}

public class QItem: Node 
{
	public int row;
	public int col;
	public int dist;
	public QItem prev;

	public QItem(int row, int col, int dist, QItem prev)
	{
		this.row = row;
		this.col = col;
		this.dist = dist;
		this.prev = prev;
	}

	
	public override string ToString()
	{
		if(prev != null)
		{
			return "C"+ prev.row+", "+prev.col+")";
		}
		return null;
	}
}

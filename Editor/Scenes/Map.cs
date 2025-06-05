using System;
using System.Threading;
using Editor.Scenes.AutoLoads;
using Godot;

namespace Editor.Scenes
{
	/**
	 * @desc: This class is for handling both the front end array and back end of the tiles and the validitiy of the
	 * scenario
	 */
	public class Map : TileMap
	{
		private int _currentTile = 1;
		private static int _width = 10;
		private static int _height = 10;
		private int[,] _tiles = new int[_width, _height];
		private int _cityCount = 0;
		private int _heliCount = 0;

		private PermaScene _ps;
		private WHPerma _wh;

		// Called when the node enters the scene tree for the first time.
		public override void _Ready()
		{
			Clear();
			_ps = GetNode<PermaScene>("/root/PermaScene");


			//Debug: GD.Print(_ps.getScenario());
			_wh = GetNode<WHPerma>("/root/WHPerma");
			_width = _wh.getWidth();
			_height = _wh.getHeight();
			//Debug: GD.Print(_width + " " + _height);
			_tiles = new int[_width, _height];
			for (int i = 0; i < _tiles.GetLength(0); i++)
			{
				for (int j = 0; j < _tiles.GetLength(1); j++)
				{
					_tiles[i, j] = 2;
					SetCell(i, j, 2);
				}
			}

			if (_ps.getScenario() != null)
			{
				Clear();
				_tiles = _ps.getScenario();
				_width = _tiles.GetLength(0);
				_height = _tiles.GetLength(1);
				for (int i = 0; i < _tiles.GetLength(0); i++)
				{
					for (int j = 0; j < _tiles.GetLength(1); j++)
					{
						if (_tiles[i, j] == 0)
							_cityCount++;
						if (_tiles[i, j] == 5)
							_heliCount++;

						SetCell(i, j, _tiles[i, j]);
					}
				}
			}

			//set scenario into permascene
			_ps.addScenario(_tiles);
		}

		public void SetCurrentTile(int i)
		{
			_currentTile = i;
		}

		public int GetCurrentTile()
		{
			return _currentTile;
		}

		//Sets the tile at the clicked position if possible (No more than 2 cities)
		public void Set(Vector2 wm, int tile)
		{
			//field out of bound
			if (wm.x < 0 || wm.x >= _width || wm.y < 0 || wm.y >= _height)
			{
				return;
			}

			//already 2 cities placed
			if (_cityCount >= 2 && tile == 0 && _tiles[(int)wm.x, (int)wm.y] != 0)
			{
				return;
			}

			//place city or replace (remove) city with different field

			if (_cityCount < 2 && tile == 0 && _tiles[(int)wm.x, (int)wm.y] != 0)
			{
				_cityCount++;
			}
			else if (tile != 0 && _tiles[(int)wm.x, (int)wm.y] == 0)
			{
				_cityCount--;
			}
			
			//place city or replace (remove) city with different field

			if (tile == 5 && _tiles[(int)wm.x, (int)wm.y] != 5)
			{
				_heliCount++;
			}
			else if (tile != 5 && _tiles[(int)wm.x, (int)wm.y] == 5)
			{
				_heliCount--;
			}

			_tiles[(int)wm.x, (int)wm.y] = tile;
			//Debug: GD.Print("Index: " + _tiles[(int)wm.x, (int)wm.y] + ", cities: " + _cityCount);

			this.SetCellv(wm, tile);

			//write changes to permascene
			_ps.addScenario(_tiles);
		}


		public Map GetMap()
		{
			return this;
		}

		public int getCityCount()
		{
			return _cityCount;
		}

		public int getHeliCount()
		{
			return _heliCount;
		}
	}
}

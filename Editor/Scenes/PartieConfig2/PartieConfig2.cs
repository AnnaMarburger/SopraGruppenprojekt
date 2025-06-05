using System;
using System.IO;
using System.IO.Ports;
using Editor.Scenes.AutoLoads;
using Editor.Scenes.StartScreen;
using Godot;
using Newtonsoft.Json;
using File = Godot.File;

namespace Editor.Scenes.PartieConfig2
{
	
	/**
	 *@description: This is the state of the partie config which will get exported at the end.
	 */
	public class PartieConfig2 : Node2D
	{
		private PermaPartie _pp;
		private Root _r;
		private actionTime _at;
		private ActionTimeAi _ata;
		private cellularAutomaton _ca;
		private cloneProbability _cp;
		private highGroundBonusRatio _hgbr;
		private lowGroundMalusRatio _lgmr;
		private kanlySuccessProbability _ksp;
		private minPauseTime _mpt;
		private numbOfRounds _nor;
		private sandWormSpawnDistance _swsd;
		private sandWormSpeed _sws;
		private spiceMinimum _sm;
		private RegEx re = new RegEx();
		private maxStrikes _ms;
		private crashProbability _cs;



		public int _tempnumbOfRounds;
		public double _tempactionTimeUserClient;
		public double _tempactionTimeAiClient;
		public double _temphighGroundBonusRatio;
		public double _templowerGroundMalusRatio;
		public double _tempkanlySuccessProbability;
		public int _tmepspiceMinimum;
		public string _tmepcellularAutomaton;
		public int _tempsandWormSpeed;
		public int _tempsandWormSpawnDistance;
		public double _tempcloneProbability;
		public int _tempminPauseTime;
		public int _tempMaxStrikes;
		public double _tempCrashProbability;
		

		public override void _Ready()
		{
			re.Compile("B[0-8]*/S[0-8]*");
			_pp = GetNode<PermaPartie>("/root/PermaPartie");
			_r = _pp.getPartie();
			
			_at = GetNode<actionTime>("actionTime");
			_ata = GetNode<ActionTimeAi>("actionTimeAi");
			_ca = GetNode<cellularAutomaton>("cellularAutomaton");
			_cp = GetNode<cloneProbability>("cloneProbability");
			_hgbr = GetNode<highGroundBonusRatio>("highGroundBonusRatio");
			_lgmr = GetNode<lowGroundMalusRatio>("lowGroundMalusRatio");
			_ksp = GetNode<kanlySuccessProbability>("kanlySuccessProbability");
			_mpt = GetNode<minPauseTime>("minPauseTime");
			_nor = GetNode<numbOfRounds>("numbOfRounds");
			_swsd = GetNode<sandWormSpawnDistance>("sandWormSpawnDistance");
			_sws = GetNode<sandWormSpeed>("sandWormSpeed");
			_sm = GetNode<spiceMinimum>("spiceMinimum");
			_ms = GetNode<maxStrikes>("maxStrikes");
			_cs = GetNode <crashProbability>("crashProbability");
			
			//setting values
			SetTextFromRoot();

			//setting temp values
			_tempactionTimeUserClient = _r.actionTimeUserClient;
			_tempactionTimeAiClient = _r.actionTimeAiClient;
			_tmepcellularAutomaton = _r.cellularAutomaton;
			_tempcloneProbability = _r.cloneProbability;
			_temphighGroundBonusRatio = _r.highGroundBonusRatio;
			_templowerGroundMalusRatio= _r.lowerGroundMalusRatio;
			_tempkanlySuccessProbability = _r.kanlySuccessProbability;
			_tempminPauseTime = _r.minPauseTime;
			_tempnumbOfRounds = _r.numbOfRounds;
			_tempsandWormSpawnDistance = _r.sandWormSpawnDistance;
			_tempsandWormSpeed = _r.sandWormSpeed;
			_tmepspiceMinimum= _r.spiceMinimum;
			_tempMaxStrikes = _r.maxStrikes;
			_tempCrashProbability = _r.crashProbability;

		}

		private void SetTextFromRoot()
		{
			_at.Text = _r.actionTimeUserClient.ToString();
			_ata.Text = _r.actionTimeAiClient.ToString();
			_ca.Text = _r.cellularAutomaton;
			_cp.Text = _r.cloneProbability.ToString();
			_hgbr.Text = _r.highGroundBonusRatio.ToString();
			_lgmr.Text = _r.lowerGroundMalusRatio.ToString();
			_ksp.Text = _r.kanlySuccessProbability.ToString();
			_mpt.Text = _r.minPauseTime.ToString();
			_nor.Text = _r.numbOfRounds.ToString();
			_swsd.Text = _r.sandWormSpawnDistance.ToString();
			_sws.Text = _r.sandWormSpeed.ToString();
			_sm.Text = _r.spiceMinimum.ToString();
			_ms.Text = _r.maxStrikes.ToString();
			_cs.Text = _r.crashProbability.ToString();
		}

		public void _on_TextureButton_pressed()
		{
			SetValuesFromInput();
			GetTree().ChangeScene("res://Scenes/PartieConfiguration/CharacterConfig.tscn");
		}

		public void _save_on_selected_path(string path)
		{
			//save the current settings into permapartie
			SetValuesFromInput();
			GD.Print(_r);
			
			_save_to_JSON(path);
		}

		private void SetValuesFromInput()
		{
			_r.actionTimeUserClient = _tempactionTimeUserClient;
			_r.actionTimeAiClient = _tempactionTimeAiClient;
			_r.cellularAutomaton = _tmepcellularAutomaton;
			_r.cloneProbability = _tempcloneProbability;
			_r.highGroundBonusRatio = _temphighGroundBonusRatio;
			_r.lowerGroundMalusRatio = _templowerGroundMalusRatio;
			_r.kanlySuccessProbability = _tempkanlySuccessProbability;
			_r.minPauseTime = _tempminPauseTime;
			_r.numbOfRounds = _tempnumbOfRounds;
			_r.sandWormSpawnDistance = _tempsandWormSpawnDistance;
			_r.sandWormSpeed = _tempsandWormSpeed;
			_r.spiceMinimum = _tmepspiceMinimum;
			_r.maxStrikes = _tempMaxStrikes;
			_r.crashProbability = _tempCrashProbability;


		}


		public void _save_to_JSON(string path)
		{
			
			//Replace 'res:/' from given path with a single dot
			string[] pathArray = path.Split("res:/");
			string pathWithoutRes = "." + pathArray[1];

			//Write the given object into the json file
			JsonSerializer jsonSerializer = new JsonSerializer();
			StreamWriter sw = new StreamWriter(pathWithoutRes);
			JsonWriter jsonWriter = new JsonTextWriter(sw);
			
			jsonSerializer.Serialize(jsonWriter, _r);
			
			jsonWriter.Close();
			sw.Close();
		}

		
		//The following methods are for setting the values on the screen
		public void _on_numbOfRounds_focus_exited()
		{
			var input = _nor.Text;
			if (!isInteger(input)||input.ToInt() <1)
			{
				GD.PrintErr("Not An Integer or smaller 1");
				return;
			}

			_tempnumbOfRounds = input.ToInt();
		}

		public void _on_actionTime_focus_exited()
		{
			var input = _at.Text;
			if (!IsFloat(input)||input.ToFloat()<0)
			{
				GD.PrintErr("not A number or negative");
				return;
			}

			_tempactionTimeUserClient = input.ToFloat();
		}
		
		public void _on_actionTimeAi_focus_exited()
		{
			var input = _ata.Text;
			if (!IsFloat(input)||input.ToFloat()<0)
			{
				GD.PrintErr("not A number or negative");
				return;
			}

			_tempactionTimeAiClient = input.ToFloat();
		}

		public void _on_highGroundBonusRatio_focus_exited()
		{
			var input = _hgbr.Text;
			if (!IsFloat(input) || input.ToFloat() < 0 )
			{
				GD.PrintErr("Not A Number or negative");
				return;
			}

			_temphighGroundBonusRatio = input.ToFloat();
		}

		public void _on_kanlySuccessProbability_focus_exited()
		{
			var input = _ksp.Text;
			if (!IsFloat(input) || input.ToFloat() < 0 ||input.ToFloat()>1 )
			{
				GD.PrintErr("Not A Number or not in between 0 and 1");
				return;
			}

			_tempkanlySuccessProbability = input.ToFloat();
		}

		public void _on_spiceMinimum_focus_exited()
		{
			var input = _sm.Text;
			if (!isInteger(input) || input.ToInt() < 0)
			{
				GD.PrintErr("Not a Number or negative");
				return;
			}

			_tmepspiceMinimum = input.ToInt();
		}
		

		public void _on_cellularAutomaton_focus_exited()
		{
			var input = _ca.Text;
			if (re.Search(input)==null )
			{
				GD.PrintErr("not a RegEx of form "+re.GetPattern());
				return;
			}

			_tmepcellularAutomaton = input;
		}

		public void _on_sandWormSpeed_focus_exited()
		{
			var input = _sws.Text;
			if (!isInteger(input) || input.ToInt() < 0)
			{
				GD.PrintErr("Not An Integer or negative");
				return;
			}

			_tempsandWormSpeed = input.ToInt();
		}

		public void _on_sandWormSpawnDistance_focus_exited()
		{
			var input = _swsd.Text;
			if (!isInteger(input) || input.ToInt() < 0)
			{
				GD.PrintErr("Not An Integer or negative");
				return;
			}

			_tempsandWormSpawnDistance = input.ToInt();
		}

		public void _on_cloneProbability_focus_exited()
		{
			var input = _cp.Text;
			if (!IsFloat(input) || input.ToFloat() < 0 ||input.ToFloat()>1 )
			{
				GD.PrintErr("Not A Number or not in between 0 and 1");
				return;
			}

			_tempcloneProbability = input.ToFloat();
		}

		public void _on_minPauseTime_focus_exited()
		{
			var input = _mpt.Text;
			if (!IsFloat(input) || input.ToFloat() < 0)
			{
				GD.PrintErr("Not An Integer or negative");
				return;
			}

			_tempminPauseTime = input.ToInt();
		}

		public void _on_lowGroundMalusRatio_focus_exited()
		{
			var input = _lgmr.Text;
			if (!IsFloat(input) || input.ToFloat() < 0 )
			{
				GD.PrintErr("Not A Number or negative");
				return;
			}

			_templowerGroundMalusRatio = input.ToFloat();
		}

		public void _on_maxStrikes_focus_exited()
		{
			var input = _ms.Text;
			if (!isInteger(input) || input.ToInt() < 0)
			{
				GD.Print("Not A Number or negative");
				return;
			}
			GD.Print(_r.maxStrikes);
			_tempMaxStrikes = input.ToInt();
		}

		public void _on_crashProbability_focus_exited()
		{
			var input = _cs.Text;
			if (!IsFloat(input) || input.ToFloat() < 0 || input.ToFloat() > 1)
			{
				GD.Print("Not a Number or not between 0 and 1");
				return;
			}

			_tempCrashProbability = input.ToFloat();
		}
		
		public bool IsFloat(string value) {
			
			float floatValue;
			return float.TryParse(value, out floatValue);
			
		}

		public bool isInteger(string value)
		{
			int intValue;
			return Int32.TryParse(value, out intValue);
		}
	}
}

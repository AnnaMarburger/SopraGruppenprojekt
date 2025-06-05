using System;
using Editor.Scenes.AutoLoads;
using Godot;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Schema;

#pragma warning disable CS0618

namespace Editor.Scenes.StartScreen
{

	
	public class StartScreen : Control
	{
		private PermaPartie _pp;
		private string _jsonSchemaScenario;
		private string _jsonSchemaPartie;
		//Default Json Partie Config
		private string _json="{\n  \"noble\":{\n    \"maxHP\":20,\n    \"maxMP\":21,\n    \"maxAP\":22,\n    \"damage\":4,\n    \"inventorySize\":8,\n    \"healingHP\":6\n  },\n  \"mentat\":{\n    \"maxHP\":20,\n    \"maxMP\":21,\n    \"maxAP\":22,\n    \"damage\":4,\n    \"inventorySize\":8,\n    \"healingHP\":6\n  },\n  \"beneGesserit\":{\n    \"maxHP\":20,\n    \"maxMP\":21,\n    \"maxAP\":22,\n    \"damage\":4,\n    \"inventorySize\":8,\n    \"healingHP\":6\n  },\n  \"fighter\":{\n    \"maxHP\":20,\n    \"maxMP\":21,\n    \"maxAP\":22,\n    \"damage\":4,\n    \"inventorySize\":8,\n    \"healingHP\":6\n  },\n  \"numbOfRounds\":10,\n  \"actionTime\":0.5,\n  \"highGroundBonusRatio\":0.5,\n  \"lowerGroundMalusRatio\":0.5,\n  \"kanlySuccessProbability\":0.5,\n  \"spiceMinimum\":10,\n  \"cellularAutomaton\":\"B2/S3\",\n  \"sandWormSpeed\":10,\n  \"sandWormSpawnDistance\":10,\n  \"cloneProbability\":0.5,\n   \"crashProbability\":0.5\n}";
		private PermaScene _a;

		public override void _Ready()
		{
			_pp = GetNode<PermaPartie>("/root/PermaPartie");
			_jsonSchemaScenario = GetLinesFromFilesAsStrings("./Scenes/StartScreen/scenario_config_schema.json");
			_jsonSchemaPartie = GetLinesFromFilesAsStrings("./Scenes/StartScreen/partie-konfig-schema.json");
			
			JObject jInput = JObject.Parse(_json);
			Root r = JsonConvert.DeserializeObject<Root>(_json);
			_a = GetNode<PermaScene>("/root/PermaScene");
			_pp.setPartie(r);
		   

		}

		public void _on_FileDialog_file_selected(String path)
		{
			
			//Loads JsonFile
			_json = GetLinesFromFilesAsStrings(path.Substring(6));
			
			//Parses both validifiers
			JSchema jsp = JSchema.Parse(_jsonSchemaPartie);
			JSchema jss = JSchema.Parse(_jsonSchemaScenario);
			
			//Convert Input for parsing
			JObject jInput = JObject.Parse(_json);
			
			if (jInput.IsValid(jsp))
			{
				Root r = JsonConvert.DeserializeObject<Root>(_json);
				_pp.setPartie(r);
				GetTree().ChangeScene("res://Scenes/PartieConfiguration/CharacterConfig.tscn");
			}

			if (jInput.IsValid(jss))
			{
				SzenarioRoot sr = JsonConvert.DeserializeObject<SzenarioRoot>(_json);
				//If Scenario is invialid, returns (Exits)
				if(!sr.valid())
					return;
				if (sr != null) _a.addScenario(sr.toIds());
				//GD.Print(sr.toIds());
				GetTree().ChangeScene("res://Scenes/newConfigScene/newEditorScreen.tscn");
			}
			
			



		}

		public void _on_existingFileButton2_pressed()
		{
			GetTree().ChangeScene("res://Scenes/PartieConfiguration/CharacterConfig.tscn");
		}
		
		//Converts file to string
		private static string GetLinesFromFilesAsStrings(string path)
		{
			var input = System.IO.File.ReadLines(path);
			string json = "";
			foreach (var line in input)
			{
				json += line + "\n";
			}

			return json;
		}
	}
}

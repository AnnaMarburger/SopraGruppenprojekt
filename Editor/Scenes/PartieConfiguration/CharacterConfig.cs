using System.Dynamic;
using Editor.Scenes.AutoLoads;
using Godot;

namespace Editor.Scenes.PartieConfiguration
{
	/**
	 * @description: This class stores the values for the given type
	 */
	public class CharacterConfig : Node2D
	{
		private PermaPartie _pp;
		private StartScreen.Noble _noble;
		private StartScreen.Fighter _fighter;
		private StartScreen.Mentat _mentat;
		private StartScreen.BeneGesserit _beneGesserit;

		private Noble _nobleConfig;
		private Fighter _fighterConfig;
		private Mentat _mentatConfig;
		private BeneGesserit _beneGesseritConfig;
		
		public override void _Ready()
		{
			_pp = GetNode<PermaPartie>("/root/PermaPartie");
			
			_noble = _pp.getNoble();
			_fighter = _pp.getFighter();
			_mentat = _pp.getMentat();
			_beneGesserit = _pp.getBeneGesserit();


			_nobleConfig = GetNode<Noble>("Noble");
			_fighterConfig = GetNode<Fighter>("Fighter");
			_mentatConfig = GetNode<Mentat>("Mentat");
			_beneGesseritConfig = GetNode<BeneGesserit>("BeneGesserit");

			_nobleConfig.SetValues(_noble);
			_fighterConfig.SetValues(_fighter);
			_mentatConfig.SetValues(_mentat);
			_beneGesseritConfig.SetValues(_beneGesserit);


		}
		

		public void _on_NextValues_pressed()
		{
			_pp.getPartie().noble = _nobleConfig.getNoble();
			_pp.getPartie().fighter = _fighterConfig.getFighter();
			_pp.getPartie().mentat = _mentatConfig.getMentat();
			_pp.getPartie().beneGesserit = _beneGesseritConfig.getBeneGesserit();
			GetTree().ChangeScene("res://Scenes/PartieConfig2/PartieConfig2.tscn");
		}
		
	}
}


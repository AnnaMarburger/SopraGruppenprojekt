using System;
using System.Globalization;
using Godot;

namespace Editor.Scenes.PartieConfiguration
{
    /**
     * @description: This class stores the values for the given type
     */
    public class Fighter : Node2D
    {
        private float _maxHP;
        private int _maxMP;
        private int _maxAP;
        private float _damage;
        private int _inventorySize;
        private float _healingHP;

        private LineEdit _maxHPEdit;
        private LineEdit _maxAPEdit;
        private LineEdit _maxMPEdit;
        private LineEdit _damageEdit;
        private LineEdit _healingHPEdit;
        private LineEdit _inventorySizeEdit;
        public override void _Ready()
        {
            _maxAPEdit = GetNode<LineEdit>("maxAP");
            _maxMPEdit = GetNode<LineEdit>("maxMP");
            _maxHPEdit = GetNode<LineEdit>("maxHP");
            _damageEdit = GetNode<LineEdit>("damage");
            _inventorySizeEdit = GetNode<LineEdit>("inventorySize");
            _healingHPEdit = GetNode<LineEdit>("healingHP");
        }

        public void SetValues(StartScreen.Fighter n)
        {
            _maxHP = n.maxHP;
            _maxAP = n.maxAP;
            _maxMP = n.maxMP;
            _damage = n.damage;
            _inventorySize = n.inventorySize;
            _healingHP = n.healingHP;

            _maxAPEdit.Text = _maxAP.ToString();
            _maxMPEdit.Text = _maxMP.ToString();
            _maxHPEdit.Text = _maxHP.ToString(CultureInfo.InvariantCulture);
            _damageEdit.Text = _damage.ToString(CultureInfo.InvariantCulture);
            _inventorySizeEdit.Text = _inventorySize.ToString(CultureInfo.InvariantCulture);
            _healingHPEdit.Text = _healingHP.ToString();
        }
        
        public void _on_maxHP_focus_exited()
        {
            try
            {
                var maxHp = _maxHPEdit.Text.ToInt();
                if (maxHp < 0)
                    throw new Exception("Must be at least 0");
                _maxHP = maxHp;
                
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
        }

        public void _on_maxMP_focus_exited()
        {
            try
            {
                var input = _maxMPEdit.Text;
                if (!isInteger(input))
                    throw new Exception("Not an Integer");
                var maxMp = input.ToInt();
                if (maxMp < 0)
                    throw new Exception("Must be at least 0");
                _maxMP = maxMp;
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
        }

        public void _on_maxAP_focus_exited()
        {
            try
            {
                var input = _maxAPEdit.Text;
                if (!isInteger(input))
                    throw new Exception("Not an Integer");
                var maxAp = input.ToInt();
                if (maxAp < 0)
                    throw new Exception("Must be at least 0");
                _maxAP = maxAp;
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
        }

        public void _on_inventorySize_focus_exited()
        {
            try
            {
                var input = _inventorySizeEdit.Text;
                if (!isInteger(input))
                    throw new Exception("Not an Integer");
                var inventorySize = input.ToInt();
                if (inventorySize < 1)
                    throw new Exception("Must be at least 1");
                _inventorySize = inventorySize;
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
        }

        public void _on_damage_focus_exited()
        {
            try
            {
                var damage = _damageEdit.Text.ToFloat();
                if (damage < 0)
                    throw new Exception("Must be at least 0");
                _damage = damage;
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
        }

        public void _on_healingHP_focus_exited()
        {
            try
            {
                var healingHP = _healingHPEdit.Text.ToFloat();
                if (healingHP < 0)
                    throw new Exception("Must be at least 0");
                _healingHP = healingHP;
                
            }
            catch (Exception e)
            {
                GD.Print(e);
                
            }
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
        
        public StartScreen.Fighter getFighter()
        {
            StartScreen.Fighter fighter= new StartScreen.Fighter();
            fighter.damage = _damage;
            fighter.healingHP = _healingHP;
            fighter.maxAP = _maxAP;
            fighter.maxHP = _maxHP;
            fighter.maxMP = _maxMP;
            fighter.inventorySize = _inventorySize;
            return fighter;
        }
    }
}

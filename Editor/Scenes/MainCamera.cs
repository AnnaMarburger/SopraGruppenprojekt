using Godot;

namespace Editor.Scenes
{
    public class MainCamera : Godot.Camera2D
    {
        private Vector2 _previousPosition = new Vector2(0, 0);
        private bool _moveCamera = false;

        //These Values are for the zooming mechanic with the camera
        private const double MinZoom = 0.5;
        private const double MaxZoom = 2.5;
        private const double ZoomFactor = 0.1;
        private const double ZoomDuration = 0.2;
        private double _zoomLevel = 1.0;

        private anim _t;
        

        // Called when the node enters the scene tree for the first time.
        public override void _Ready()
        {
            _t = GetNode<anim>("anim");
        }
        

        public override void _Input(InputEvent @event)
        {
            switch (@event)
            {
                //Middle Mouse Button triggers Moving
                case InputEventMouseButton eventMouseButton when @event.IsActionPressed("middle"):
                {
                    GetTree().SetInputAsHandled();
                    if (@event.IsPressed())
                    {
                        
                        _previousPosition = eventMouseButton.Position;
                        _moveCamera = true;
                    }
                    

                    break;
                }
                //On release stops moving
                case InputEventMouseButton eventMouseButton when @event.IsActionReleased("middle"):
                {
                    
                    GetTree().SetInputAsHandled();
                    _moveCamera = false;
                    break;
                }
                //If mouse is moved when middle is pressed, moves map
                case InputEventMouseMotion eventMouseMotion when _moveCamera:
                    GetTree().SetInputAsHandled();
                    Position += (_previousPosition - eventMouseMotion.Position);
                    _previousPosition = eventMouseMotion.Position;
                    break;
            }

            //Zoom Button is +
            if (@event.IsActionPressed("zoom_in"))
            {
                _zoomLevel -= ZoomFactor;
                _setZoomLevel((float)_zoomLevel);
            }
            //Zoom Button is -
            if (@event.IsActionPressed("zoom_out"))
            {
                _zoomLevel += ZoomFactor;
                _setZoomLevel((float)_zoomLevel);
            }
            
            

        }
        
        
        //Sets the zoom level ones when the scene is laoded
        private void _setZoomLevel(float value)
        {
            if (_zoomLevel < MinZoom)
                _zoomLevel = MinZoom;
            if (_zoomLevel > MaxZoom)
                _zoomLevel = MaxZoom;
            _t.InterpolateProperty(this, "zoom", Zoom,
                new Vector2((float)_zoomLevel, (float) _zoomLevel),(float)ZoomDuration, Godot.Tween.TransitionType.Sine,
                Godot.Tween.EaseType.Out);
            _t.Start();
        }
    }
}

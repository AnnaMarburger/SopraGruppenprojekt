using Client.Scenes.NetComClasses;

namespace Client.Scenes
{
    public interface Observable
    {
        public void parseMessage(string data);
    }
}
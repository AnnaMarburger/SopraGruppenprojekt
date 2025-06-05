using System.Runtime.CompilerServices;
using Godot;

namespace Client.Scenes.NetComClasses
{
	public class Debug : AbstractMessage
	{
		public int code { get; set; }
		public string explanation { get; set; }

		public Debug(int code, string explanation)
		{
			this.code = code;
			this.explanation = explanation;
			this.type = MessageType.DEBUG;
		}
	}
}

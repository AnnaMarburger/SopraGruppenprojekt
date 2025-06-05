package messages;

import com.google.gson.Gson;
import messages.data.MessageType;
import shared_data.CONST;

/**
 * This class represents an abstract message.
 */
public abstract class Message {

  public final MessageType type; // type of message
  public final String version; // version of message

  protected Message(final MessageType type) {
    this.type = type;
    this.version = CONST.VERSION;
  }

  @Override
  public String toString() {
    var g = new Gson();
    return g.toJson(this);
  }
}

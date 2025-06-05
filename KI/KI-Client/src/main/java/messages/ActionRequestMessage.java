package messages;

import messages.data.MessageType;
import messages.data.dictionary.ActionSpecs;
import shared_data.Action;

/**
 * This class represents a action request message.
 */
public class ActionRequestMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final Action action; // requested action
  public final ActionSpecs specs; // specifications of the requested action

  public ActionRequestMessage(final int clientID, final int characterID, final Action action,
      final ActionSpecs specs) {
    super(MessageType.ACTION_REQUEST);
    this.clientID = clientID;
    this.characterID = characterID;
    this.action = action;
    this.specs = specs;
  }
}

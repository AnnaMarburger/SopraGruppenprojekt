package messages;

import messages.data.MessageType;
import messages.data.dictionary.ActionSpecs;
import shared_data.Action;

/**
 * This class represents an action demand message.
 */
public class ActionDemandMessage extends Message {

  public final int clientID; // client id
  public final int characterID; // character id
  public final Action action; // demanded action
  public final ActionSpecs specs; // specification of the demanded action

  public ActionDemandMessage(final int clientID, final int characterID, final Action action,
      final ActionSpecs specs) {
    super(MessageType.ACTION_DEMAND);
    this.clientID = clientID;
    this.characterID = characterID;
    this.action = action;
    this.specs = specs;
  }
}

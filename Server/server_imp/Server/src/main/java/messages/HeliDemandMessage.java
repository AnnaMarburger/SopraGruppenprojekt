package messages;

import messages.data.MessageType;
import shared_data.Position;

/**
 * This class represents a heli request message.
 */
public class HeliDemandMessage extends Message {

    public final int clientID; // client id
    public final int characterID; // character id
    public final Position target; // specifications of the requested movement
    public final boolean crash;

    public HeliDemandMessage(final int clientID, final int characterID,
                              final Position target, final boolean crash) {
        super(MessageType.HELI_DEMAND);
        this.clientID = clientID;
        this.characterID = characterID;
        this.target = target;
        this.crash = crash;
    }
}

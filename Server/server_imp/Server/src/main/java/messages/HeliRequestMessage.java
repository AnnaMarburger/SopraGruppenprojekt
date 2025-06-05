package messages;

import messages.data.MessageType;
import shared_data.Position;

/**
 * This class represents a heli request message.
 */
public class HeliRequestMessage extends Message {

    public final int clientID; // client id
    public final int characterID; // character id
    public final Position target; // specifications of the requested movement

    public HeliRequestMessage(final int clientID, final int characterID,
                                  final Position target) {
        super(MessageType.HELI_REQUEST);
        this.clientID = clientID;
        this.characterID = characterID;
        this.target = target;
    }
}
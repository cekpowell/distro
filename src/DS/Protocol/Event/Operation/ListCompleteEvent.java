package DS.Protocol.Event.Operation;

import Network.Protocol.Event.NetworkEvent;

/**
 * Represents the event of a LIST operation completing.
 */
public class ListCompleteEvent extends NetworkEvent{

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that has been stored.
     */
    public ListCompleteEvent(){
        super("'LIST' operation complete.");
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    
}

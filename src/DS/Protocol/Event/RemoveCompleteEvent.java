package DS.Protocol.Event;

import Network.Protocol.Event.NetworkEvent;

/**
 * Represents the event of a REMOVE operation completing.
 */
public class RemoveCompleteEvent extends NetworkEvent{
    
    // member variables
    private String filename;

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that has been stored.
     */
    public RemoveCompleteEvent(String filename){
        super("'REMOVE' operation complete for file : '" + filename + "'.");
        this.filename = filename;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }
}
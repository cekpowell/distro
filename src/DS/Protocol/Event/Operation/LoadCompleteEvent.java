package DS.Protocol.Event.Operation;

import Network.Protocol.Event.NetworkEvent;

/**
 * Represents the event of a LOAD operation completing.
 */
public class LoadCompleteEvent extends NetworkEvent{
    
    // member variables
    private String filename;
    
    /**
     * Class constructor.
     * 
     * @param filename The name of the file that has been stored.
     */
    public LoadCompleteEvent(String filename){
        super("'LOAD' operation complete for file : '" + filename + "'.");
        this.filename = filename;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }
}

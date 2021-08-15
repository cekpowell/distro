package DS.Protocol.Event;

import Network.Protocol.Event.NetworkEvent;

/**
 * Represents the event of a STORE operation being completed.
 */
public class StoreCompleteEvent extends NetworkEvent{

    // member variables
    private String filename;
    private int filesize;
    
    /**
     * Class constructor.
     * 
     * @param filename The name of the file that has been stored.
     * @param filesize The size of the file that has been stored.
     */
    public StoreCompleteEvent(String filename, int filesize){
        super("'STORE' operation complete for file : '" + filename + "' of size : " + filesize + " bytes.");
        this.filename = filename;
        this.filesize = filesize;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }

    public int getFilesize(){
        return this.filesize;
    }
}

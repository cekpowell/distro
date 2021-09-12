package DS.Protocol.Event.Rebalance;

import Network.Protocol.Event.NetworkEvent;

/**
 * Event for the case Dstore recieves a file from another Dstore
 * during a rebalance.
 */
public class RebalanceStoreCompleteEvent extends NetworkEvent{

    // member variables
    private String filename;
    private int filesize;

    /**
     * Class constructor.
     */
    public RebalanceStoreCompleteEvent(String filename, int filesize){
        super("REBALANCE_STORE request complete for file : '" + filename + "'' of size : " + filesize + " bytes.");
        this.filename = filename;
        this.filesize = filesize;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }

    public int getFileSize(){
        return this.filesize;
    }
}

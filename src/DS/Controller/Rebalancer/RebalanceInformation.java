package DS.Controller.Rebalancer;

import java.util.ArrayList;

import DS.Protocol.Protocol;
import DS.Protocol.Token.TokenType.FileToSend;

/**
 * Stores the files that are being sent/removed during a rebalance.
 */
public class RebalanceInformation{

    // member variables
    private ArrayList<FileToSend> filesToSend;
    private ArrayList<String> filesToRemove;

    /**
     * Class constructor.
     * 
     */
    public RebalanceInformation(){
        // initializing
        this.filesToSend = new ArrayList<FileToSend>();;
        this.filesToRemove = new ArrayList<String>();
    }

    ////////////////////
    // HELPER METHODS //
    ////////////////////

    /**
     * Returns the REBALANCE message for this rebalance information.
     * 
     * @return The REBALANCE message for this rebalance information.
     */
    public String getRebalanceMessage(){
        return Protocol.getRebalanceMessage(this.getFilesToSend(), this.getFilesToRemove());
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ArrayList<FileToSend> getFilesToSend(){
        return this.filesToSend;
    }

    public ArrayList<String> getFilesToRemove(){
        return this.filesToRemove;
    }
}
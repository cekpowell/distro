package DS.Controller.Rebalancer;

import java.util.HashMap;

import DS.Protocol.Token.TokenType.FileToSend;

/**
 * Represents a system that has been rebalanced. Stores information
 * on the file distribution within the rebalanced system, as well as
 * the rebalanc information.
 */
public class RebalancedSystem{

    // member variables
    private System system; 
    private HashMap<Integer, RebalanceInformation> rebalanceInformation;

    /**
     * Class constructor.
     * 
     * @param system The state of the system followinng it's rebalance.
     * @param rebalanceInformation The changes that have been made to rebalance the system.
     */
    public RebalancedSystem(System system, HashMap<Integer, RebalanceInformation> rebalanceInformation){
        // initializing
        this.system = system;
        this.rebalanceInformation = rebalanceInformation;
    }

    ///////////////////////////////////////
    // CONFIGURING REBALANCE INFORMATION //
    ///////////////////////////////////////

    /**
     * Adds a FileToSend to the message.
     * 
     * @param fileToSend The file to be sent.
     */
    public void addFileToSend(Integer dstoreSendingFile, FileToSend fileToSend){
        // adding the file to send to the rebalance information
        this.rebalanceInformation.get(dstoreSendingFile).getFilesToSend().add(fileToSend);

        // updating the file distribution
        this.system.updateFromRebalanceInformation(this.rebalanceInformation);
    }

    /**
     * Adds a file to be removed to the list.
     * 
     * @param fileToRemove The file to be removed.
     */
    public void addFileToRemove(Integer dstoreRemovingFile, String fileToRemove){
        // adding the file to send to the rebalance information
        this.rebalanceInformation.get(dstoreRemovingFile).getFilesToRemove().add(fileToRemove);

        // updating the file distribution
        this.system.updateFromRebalanceInformation(this.rebalanceInformation);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public System getSystem(){
        return this.system;
    }

    public HashMap<Integer, RebalanceInformation> getRebalanceInformation(){
        return this.rebalanceInformation;
    }

    public void setRebalanceInformation(HashMap<Integer, RebalanceInformation> rebalanceInformation){
        this.rebalanceInformation = rebalanceInformation;
    }
}

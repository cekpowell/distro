package Index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import Index.State.OperationState;
import Network.Connection;

/**
 * Controller that manages an Index model.
 * 
 * An Index Manager creates a new Index instance when it is
 * instantiated, and provides methods that allow for the owner
 * of the manager to interact with the underlying Index.
 */
public class Index {

    // member variables
    private volatile ArrayList<DstoreIndex> dstores;
    private volatile int minDstores;

    /**
     * Class constructor.
     * 
     * Creates a new Index instance to be managed.
     */
    public Index(int minDstores){
        this.dstores = new ArrayList<DstoreIndex>();
        this.minDstores = minDstores;
    }


    //////////////////////////
    // CONFIGURING DSTORES ///
    //////////////////////////


    /**
     * Adds the given Dstore to the index.
     * 
     * @param port The port the Dstore is listening on.
     * @param connection The connection between the Controller and the Dstore.
     */
    public void addDstore(Integer port, Connection connection){
        // adding the dstore to the list of dstores
        this.dstores.add(new DstoreIndex(port, connection));
    }

    /**
     * Removes the given Dstore from the system.
     * 
     * @param port The port of the Dstore to be removed from the system.
     */
    public void removeDstore(Connection dstore){
        // removing the Dstore from the list of Dstores
        this.dstores.remove(this.getDstoreFromConnection(dstore));
    }


    ///////////////////
    // STORING FILES //
    ///////////////////


    /**
     * Starts the process of adding a given file to the system by adding it to
     * the index.
     * 
     * @param file The name of the file being added.
     */
    public ArrayList<Integer> startStoring(String filename, int filesize) throws Exception{
        // throwing exception if not enougn dstores have joined yet
        if(this.dstores.size() < this.minDstores){
            throw new Exception();
        }

        // getting the list of dstores that the file needs to be stored on.
        ArrayList<Integer> dstoresToStoreOn = this.getDstoresToStoreOn();

        // adding dstores the file is stored on to the the index 
        for(Integer port : dstoresToStoreOn){
            // adding the file to the dstore state
            this.getDstoreFromPort(port).addFile(filename, filesize);
        }

        // returning the list of dstores the file needs to be stored on
        return dstoresToStoreOn;
    }

    /**
     * Updates the index based on a STORE_ACK that was recieved from the given Dstore.
     * 
     * @param port The port of the Dstore that send the STORE_ACK.
     * @param filename The filename the STORE_ACK is relating to
     */
    public void storeAckRecieved(Connection dstore, String filename){
        // updatiing the dstore index
        this.getDstoreFromConnection(dstore).updateFileState(filename, OperationState.STORE_ACK_RECIEVED);
    }


    ////////////////////
    // REMOVING FILES //
    ////////////////////


    /**
     * Starts the process of removing a give file from the system by updating the system index.
     * 
     * @param file The file being removed.
     */
    public ArrayList<Integer> startRemoving(String filename) throws Exception{
        // getting the list of dstores the file is stored on
        ArrayList<Integer> dstoresStoredOn = this.getDstoresStoredOn(filename);

        // updating the states
        for(Integer dstore : dstoresStoredOn){
            // updating the dstore state
            this.getDstoreFromPort(dstore).updateFileState(filename, OperationState.REMOVE_IN_PROGRESS);
        }

        // returning the dstores the file is to be removed from
        return dstoresStoredOn;
    }

    /**
     * Updates the index after a REMOVE_ACK was recieved.
     * 
     * @param port The Dstore port that the REMOVE_ACK was recieved from.
     */
    public void removeAckRecieved(Connection dstore, String filename){
        // updating the dstore index
        this.getDstoreFromConnection(dstore).updateFileState(filename, OperationState.REMOVE_ACK_RECIEVED);
    }

    //////////////////////////////////////
    // WAITING FOR OPERATION COMPLETION //
    //////////////////////////////////////

    /**
     * Waits for the state of the given file across all Dstores to match the provided expected state. Changes the global
     * state of the file to be the final state when this occurs.
     * 
     * @param filename The name of the file being tracked.
     * @param timeout The timeout for the tracking.
     * @param expectedState The expectedd state of the file.
     * @param finalState The state the file will be changed to when the operation has completed.
     * @return True if the operation completed, false if not.
     * @throws TimeoutException When the state of the file does not match the expected state within the timeout.
     */
    public boolean waitForOperationComplete(String filename, 
                                            int timeout, 
                                            OperationState expectedState, 
                                            OperationState finalState) throws TimeoutException{

        // Waiting for Operation to Complete //
        
        long timeoutStamp = System.currentTimeMillis() + timeout;

        while(!this.fileHasState(filename, expectedState)){
            if(System.currentTimeMillis() < timeoutStamp){
                Thread.onSpinWait();
            }
            else{
                // timeout occured
                this.handleOperationTimeout(filename, expectedState);

                // throwing exception
                throw new TimeoutException();
            }
        }

        // Operation Complete //

        // updating file state to the new state
        for(DstoreIndex dstore : this.dstores){
            for(DstoreFile file : dstore.getFiles()){
                if(file.getFilename().equals(filename)){
                    dstore.updateFileState(filename, finalState);
                }
            }
        }

        // returning true
        return true;
    }

    /**
     * Handles the case where an operation did not complete wthin the given timeout.
     * 
     * @param filename The filename for which the operation did not complete.
     * @param expectedState The expected state of the file.
     */
    private void handleOperationTimeout(String filename, OperationState expectedState){
        // Handling Store operation
        if(expectedState == OperationState.STORE_ACK_RECIEVED){
            // removing the file from the index
            for(int dstore : this.getDstoresStoredOn(filename)){
                this.getDstoreFromPort(dstore).removeFile(filename);
            }
        }
    }


    /////////////////
    // REBALANCING //
    /////////////////


    /**
     * Starts the process of rebalancing by updating the rebalance index to
     * REBALANCE_IN_PROGRESS.
     */
    public void startRebalancing(){
        // TODO
    }

    /**
     * Waits for all REBALANCE_COMPLETE messages to be recieved for a given file within the 
     * given timeout.
     * 
     * If not all REBALANCE_COMPLETE messages are recieved, the Dstores that did not send them
     * are considered to have disconnected and so are removed from the system. A TimeoutException is
     *  also thrown in this case.
     * 
     * @param timeout The timeout to wait for the REBALANCE_COMPELTE messages to be recieved.
     * @throws TimeoutException Thrown if not all REBALANCE_COMPLETE messages were recieved within
     * the timeout.
     */
    public boolean waitForRebalanceComplete(int timeout){
        // TODO
        return false;
    }


    //////////////////////
    // HELPER FUNCTIONS //
    //////////////////////

    /**
     * Gets the DstoreIndex object associated with the provided Dstore Server Port.
     * 
     * @param port The port the Dstore is listening on.
     * @return The DstoreIndex object associated wth the provided port, null if there 
     * was no match.
     */
    public DstoreIndex getDstoreFromPort(int port){
        // findiing the matching DstoreIndex
        for(DstoreIndex dstore : this.dstores){
            if(dstore.getPort() == port){
                return dstore;
            }
        }

        // returning null if no match found
        return null;
    }

    /**
     * Gets the DstoreIndex object assicitaed with the providedd Connection object.
     * 
     * @param connection The connection object between the Dstore and the Controller.
     * @return The DstorerIndex object associated with the provided Connectoin object, null
     * if there was no match.
     */
    public DstoreIndex getDstoreFromConnection(Connection connection){
        // finding the matching DstoreIndex
        for(DstoreIndex dstore : this.dstores){
            if(dstore.getConnection().getSocket().getPort() == connection.getSocket().getPort()){
                return dstore;
            }
        }

        // returning null if no match found
        return null;
    }

    /**
     * Gets a list of Dstores that a file can be stored on. Returns only Dstores
     * that will remain balanced after storing the file.
     * 
     * @return The list of Dstore ports that the new file can be stored on.
     */
    public ArrayList<Integer> getDstoresToStoreOn(){
        // sorting the dstores based on the number of files they contain
        Collections.sort(this.dstores);

        ArrayList<Integer> ports = new ArrayList<Integer>();

        // picking the first r dstores to store on
        for(int i =0; i < this.minDstores; i++){
            ports.add(this.dstores.get(i).getPort());
        }

        // returning the list of dstores
        return ports;
    }

    /**
     * Returns the list Dstore ports that the given file is stored on.
     * @param filename The name of the file being searched.
     */
    public ArrayList<Integer> getDstoresStoredOn(String filename){
        ArrayList<Integer> ports = new ArrayList<Integer>();
        
        // looping through all dstores and seeing if they contain the file
        for(DstoreIndex dstore : this.dstores){
            if(dstore.hasFile(filename)){
                ports.add(dstore.getPort());
            }
        }

        // file not stored on the system.
        return ports;
    }

    /**
     * Determines if the given file has the given state across all of the Dstores that
     * it is stored on.
     * 
     * @param filename The file being checked.
     * @param state The state of the file.
     * @return True if the state of the file is the provided state, false if not.
     */
    public boolean fileHasState(String filename, OperationState state){
        for(Integer port : this.getDstoresStoredOn(filename)){
            for(DstoreFile file : this.getDstoreFromPort(port).getFiles()){
                if(file.getFilename().equals(filename) && file.getState() != state){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Converts the Index to a strnig representation.
     */
    public String toString(){
        String string = "\n";

        for(DstoreIndex dstore : this.dstores){
            string += "\t" + dstore.toString() + "\n";
        }

        return string;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ArrayList<DstoreIndex> getDstores(){
        return this.dstores;
    }

    /**
     * Returns the list of ports for all Dstores on the system.
     * 
     * @return The list of ports for all Dstores on the system.
     */
    public ArrayList<Integer> getDstorePorts(){
        ArrayList<Integer> ports = new ArrayList<Integer>();

        for(DstoreIndex dstore : this.dstores){
            ports.add(dstore.getPort());
        }

        return ports;
    }

    /**
     * Returns a list of all files stored in the system.
     * 
     * @return ArrayList of all files stored in the system.
     */
    public ArrayList<String> getFiles(){
        // getting list of all files
        ArrayList<String> allFiles = new ArrayList<String>();
        for(DstoreIndex dstore : this.dstores){
            for(DstoreFile file: dstore.getFiles()){
                allFiles.add(file.getFilename());
            }
        }

        // removing duplicates and returning
        return new ArrayList<String>(new HashSet<String>(allFiles));
    }
}
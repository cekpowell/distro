package Index;

import java.util.ArrayList;
import java.util.HashMap;

import Index.State.*;

/**
 * Data structure that keeps track on the state of the system and the files stored in it.
 * 
 * Contains three individual indexes to record the system state:
 * 
 *      * 1 - A HashMap of Dstores (DstoreConnections) to the files stored on the Dstore.
 *              - Each file contained as a FileState object, which records the file name and it's state with respect to
 *              the current Dstore.
 *      * 2 - A HashMap of Files (filenames) to the Dstores that the files are stored on.
 *              - Each Dstore is contained as a DstoreState object, which records the port of the Dstore as well as the 
 *              state of the file on that Dstore.
 *      * 3 - A HashMap of Dstore ports to the rebalance state of those Dstores.
 * 
 * An Index instance must be managed by an Index Manager. The Index represents
 * the model, and the IndexManager is the controller.
 */
public class Index {

    // member variables
    private HashMap<Integer, ArrayList<FileState>> dstoreIndex;
    private HashMap<String, ArrayList<DstoreState>> fileIndex;
    private HashMap<Integer, DstoreRebalanceState> rebalanceIndex;

    /**
     * Class constructor.
     * 
     * Initialises new instances of the indexes..
     */
    public Index(){
        // initialising member variables
        this.dstoreIndex = new HashMap<Integer, ArrayList<FileState>>();
        this.fileIndex = new HashMap<String, ArrayList<DstoreState>>();
        this.rebalanceIndex = new HashMap<Integer, DstoreRebalanceState>();
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public HashMap<Integer, ArrayList<FileState>> getDstoreIndex(){
        return this.dstoreIndex;
    }

    public HashMap<String, ArrayList<DstoreState>> getFileIndex(){
        return this.fileIndex;
    }

    public HashMap<Integer, DstoreRebalanceState> getRebalanceIndex(){
        return this.rebalanceIndex;
    }
}

/**
 * Represents the state of a file with regards to a Dstore it is stored on.
 * 
 * Inverse of DstoreState.
 */
class FileState{

    // member variables
    String file;
    OperationState state;

    /**
     * Class constructor.
     * 
     * @param file The filename the state is associated with.
     */
    public FileState(String file, OperationState state){
        this.file = file;
        this.state = state;;
    }

    /**
     * Attempts to set the state of the file.
     * 
     * @param state The state the file will be set to.
     * @throws Exception Thrown in case where state cannot be changed to the provided state.
     */
    public synchronized void setState(OperationState state) throws Exception{
        /**
         * if( shouldnt be able to change to the given state...)
         *      throw exception...
         * else{
         *      change state...
         * }
         */
        this.state =  state;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFile(){
        return this.file;
    }

    public OperationState getState(){
        return this.state;
    }
}

/**
 * Represents the state of a Dstore with regards to specific file it stores.
 * 
 * Inverse of FileState.
 */
class DstoreState{

    //member variables
    int dstorePort;
    OperationState state;

    /**
     * Class constructor.
     * 
     * @param dstorePort The port of the Dstore the state is associated with.
     */
    public DstoreState(int dstorePort, OperationState state){
        this.dstorePort = dstorePort;
        this.state = state;
    }

    /**
     * Attempts to set the state of the Dstore.
     * 
     * @param state The state the Dstore will be set to.
     * @throws Exception Thrown in case where state cannot be changed to the provided state.
     */
    public synchronized void setState(OperationState state) throws Exception{
        /**
         * if( shouldnt be able to change to the given state...)
         *      throw exception...
         * else{
         *      change state...
         * }
         */
        this.state =  state;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getDstorePort(){
        return this.dstorePort;
    }

    public OperationState getState(){
        return this.state;
    }
}

/**
 * Represents the rebalance state of the Dstore.
 * 
 * Doesn't add anything on top of the RebalanceState class, but means 
 * that the user cannot change the state without using the setState method, which is
 * thread safe and handles invalid changes.
 */
class DstoreRebalanceState{

    // member variables
    RebalanceState state; // the rebalance state of the Dstore

    /**
     * Class constructor.
     * 
     */
    public DstoreRebalanceState(){
        this.state = RebalanceState.IDLE;
    }

    /**
     * Attempts to set the rebalance state of the Dstore.
     * 
     * @param state The rebalance state the Dstore will be set to.
     * @throws Exception Thrown in case where state cannot be changed to the provided state.
     */
    public synchronized void setState(RebalanceState state) throws Exception{
        /**
         * if( shouldnt be able to change to the given state...)
         *      throw exception...
         * else{
         *      change state...
         * }
         */
        this.state =  state;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public RebalanceState getState(){
        return this.state;
    }
}
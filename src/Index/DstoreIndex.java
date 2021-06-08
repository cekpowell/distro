package Index;

import java.util.ArrayList;

import Index.State.OperationState;
import Index.State.RebalanceState;
import Network.Connection;

/**
 * Represents the 'State' of a Dstore within the system. Used by the Controller
 * to keep track of the Dstores it is controlling, along with the files stored on them.
 */
public class DstoreIndex implements Comparable<DstoreIndex>{
    
    // member variables
    private int port;
    private Connection connection;
    private ArrayList<DstoreFile> files;
    private RebalanceState rebalanceState;

    /**
     * Class constructor.
     * 
     * @param port The port the dstore is listening on.
     * @param connection The controller's connection to the Dstore.
     */
    public DstoreIndex(int port, Connection connection){
        this.port = port;
        this.connection = connection;
        this.files = new ArrayList<DstoreFile>();
        this.rebalanceState = RebalanceState.IDLE;
    }

    /**
     * Adds a new file to the list of files
     * 
     * @param filename The name of the file to be added.
     * @param filesize The size of the file to be added in bytes.
     */
    public void addFile(String filename, int filesize){
        this.files.add(new DstoreFile(filename, filesize));
    }

    /**
     * Removes a given file from the index.
     * 
     * @param filename The file to be removed
     */
    public void removeFile(String filename){
        for(DstoreFile file : this.files){
            if(file.getFilename().equals(filename)){
                this.files.remove(file);
            }
        }
    }

    /**
     * Determines if the given file is stored on the Dstore.
     * 
     * @param filename The file being checkedd for.
     * @return True if the file is stored on the Dstore, false if not.
     */
    public boolean hasFile(String filename){
        for(DstoreFile file : this.files){
            if(file.getFilename().equals(filename)){
                return true;
            }
        }

        return false;
    }

    /**
     * Sets the state of a file to the given state.
     * 
     * @param filename The file having it's state changed.
     * @param state The state the file will be changed to.
     */
    public void updateFileState(String filename, OperationState state){
        for(DstoreFile file : this.files){
            if(file.getFilename().equals(filename)){
                file.setState(state);
            }
        }
    }

    /**
     * Comparator method. Compares to Dstore indexes based on the number of files
     * they have stored on them.
     * 
     * @param otherDstore
     * @return
     */
    @Override
    public int compareTo(DstoreIndex otherDstore){
        if(this.files.size() < otherDstore.getFiles().size()){
            return -1;
        }
        else if(this.files.size() == otherDstore.getFiles().size()){
            return 0;
        }
        else{
            return 1;
        }
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public ArrayList<DstoreFile> getFiles(){
        return this.files;
    }

    public RebalanceState getRebalanceState(){
        return this.rebalanceState;
    }

    public String toString(){
        return (this.port + " : " + this.files.toString());
    }
}


/**
 * Represents a file stored on a Dstore.
 * 
 * Inverse of DstoreState.
 */
class DstoreFile{

    // member variables
    private String filename;
    private int filesize;
    private OperationState state;

    /**
     * Class constructor.
     * 
     * @param file The filename the state is associated with.
     */
    public DstoreFile(String filename, int filesize){
        this.filename = filename;
        this.filesize = filesize;
        this.state = OperationState.STORE_IN_PROGRESS;
    }

    /**
     * Attempts to set the state of the file.
     * 
     * @param state The state the file will be set to.
     * @throws Exception Thrown in case where state cannot be changed to the provided state.
     */
    public synchronized void setState(OperationState state){
        /**
         * if( shouldnt be able to change to the given state...)
         *      throw exception...
         * else{
         *      change state...
         * }
         */
        this.state =  state;
    }

    public String toString(){
        return ("(" + this.filename + ", " + this.state.toString() + ")");
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

    public OperationState getState(){
        return this.state;
    }
}
package DS.Controller.Index;

import DS.Controller.Index.State.OperationState;

/**
 * Represents a file object stored on a DstoreIndex.
 */
public class DstoreFile{

    // member variables
    private String filename;
    private int filesize;
    private OperationState state;

    /**
     * Class constructor.
     * 
     * @param file The name of the file.
     * @param filesize The size of the file in bytes.
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
     */
    public synchronized void setState(OperationState state){
        this.state =  state;
    }

    /**
     * Converts the DstoreIndex to a string.
     * 
     * @return String representation of the DstoreIndex
     */
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

package DS.Controller.Rebalancer;

/**
 * Represents a file that is stored on a Dstore.
 */
public class FileOnDstore{

    // member variables
    private Integer dstore;
    private String filename;
    private int filesize;

    /**
     * Class constructor.
     *  
     * @param dstore The port of the Dstore that the file is stored on.
     * @param filename The name of the file.
     * @param filesize The size of the file in bytes.
     */
    public FileOnDstore(Integer dstore, String filename, int filesize){
        // initializing
        this.dstore = dstore;
        this.filename = filename;
        this.filesize = filesize;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public Integer getDstore(){
        return this.dstore;
    }

    public String getFilename(){
        return this.filename;
    }
    
    public int getFileSize(){
        return this.filesize;
    }
}

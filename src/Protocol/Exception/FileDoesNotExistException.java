package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where a file does not exist.
 */
public class FileDoesNotExistException extends NetworkException{

    // member variables
    private String filename;

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that already exists.
     */
    public FileDoesNotExistException(String filename){
        super("The file '" + filename + "' does not exist.");
        this.filename = filename;

    }

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that already exists.
     */
    public FileDoesNotExistException(String filename, Exception cause){
        super("The file '" + filename + "' does not exist.", cause);
        this.filename = filename;

    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }
}

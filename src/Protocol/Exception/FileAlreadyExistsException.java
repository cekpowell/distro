package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where a file already exists.
 */
public class FileAlreadyExistsException extends NetworkException{

    // member variables
    private String filename;

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that already exists.
     */
    public FileAlreadyExistsException(String filename){
        super("A file with the name '" + filename + "' already exists.");
        this.filename = filename;

    }

    /**
     * Class constructor.
     * 
     * @param filename The name of the file that already exists.
     */
    public FileAlreadyExistsException(String filename, Exception cause){
        super("A file with the name '" + filename + "' already exists.", cause);
        this.filename = filename;

    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }
}

package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for when client cannot load file content from a Dstore.
 */
public class FileLoadException extends NetworkException{
    
    // member variables
    private String filename;
    private int port;

    /**
     * Class constructor.
     * 
     * @param filename
     * @param port
     */
    public FileLoadException(String filename, int port){
        super("Unable to load file content for file '" + filename + "' from port : " + port + ".");
        this.filename = filename;
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param filename
     * @param port
     */
    public FileLoadException(String filename, int port, Exception cause){
        super("Unable to load file content for file '" + filename + "' from port : " + port + ".", cause);
        this.filename = filename;
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getFilename(){
        return this.filename;
    }

    public int getPort(){
        return this.port;
    }
}

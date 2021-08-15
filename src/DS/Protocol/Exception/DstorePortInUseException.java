package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for case where connection to Dstore terminates.
 */
public class DstorePortInUseException extends NetworkException {

    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param port The port the Dstore was trying to join on.
     */
    public DstorePortInUseException(int port){
        super("The port : " + port + " is in use by another Dstore.");
        this.port = port;
    }
}
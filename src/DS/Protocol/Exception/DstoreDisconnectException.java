package DS.Protocol.Exception;

import Network.Protocol.Exception.ConnectionTerminatedException;
import Network.Protocol.Exception.NetworkException;

/**
 * Exception for case where connection to Dstore terminates.
 */
public class DstoreDisconnectException extends NetworkException {

    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public DstoreDisconnectException(int port){
        super("The connection to Dstore on port : " + port + " was terminated.");
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public DstoreDisconnectException(int port, ConnectionTerminatedException terminationException){
        super("The connection to Dstore on port : " + port + " was terminated.", terminationException.getCause());
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

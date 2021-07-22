package Protocol.Exception;

import Network.Protocol.Exception.ConnectionTerminatedException;
import Network.Protocol.Exception.NetworkException;

/**
 * Exception for when connection to controller terminates.
 */
public class ControllerDisconnectException extends NetworkException {

    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public ControllerDisconnectException(int port){
        super("The connection to Controller on port : " + port + " was terminated.");
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public ControllerDisconnectException(int port, ConnectionTerminatedException terminationException){
        super("The connection to Controller on port : " + port + " was terminated.", terminationException.getCause());
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

package Network.Protocol.Exception;

/**
 * Exception for case where the Client heartbeat disconnects from the Server.
 */
public class ClientHeartbeatDisconnectException extends NetworkException {

    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param clientType
     * @param port
     */
    public ClientHeartbeatDisconnectException(int port, ConnectionTerminatedException terminationException){
        super("The Heartbeat Connection to Client on port : " + port + " was terminated.", terminationException.getCause());
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}
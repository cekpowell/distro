package Network.Protocol.Exception;

/**
 * Exception for the case where an unknown connector disconnects from the Sever.
 */
public class UnknownConnectorDisconnectException extends NetworkException{

    // member variables
    private int port;
    
    /**
     * Class constructor.
     * 
     * @param port The endport for the connection.
     * @param terminationException The exception raised by the connection terminating.
     */
    public UnknownConnectorDisconnectException(int port, ConnectionTerminatedException terminationException){
        super("The connection to an Unknown Connnector on port : " + port + " was terminated.", terminationException.getCause());
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

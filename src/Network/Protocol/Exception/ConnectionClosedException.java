package Network.Protocol.Exception;

/**
 * Exception for the case where the user closed the connection.
 */
public class ConnectionClosedException extends NetworkException{
    
    // member variables
    private int port;

    /**
     * Class constructor.
     * 
     * @param port The port the connection was linked to.
     */
    public ConnectionClosedException(int port){
        super("The connection to port : " + port + " was closed.");
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param port The port the connection was linked to.
     */
    public ConnectionClosedException(int port, Exception cause){
        super("The connection to port : " + port + " was closed.", cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

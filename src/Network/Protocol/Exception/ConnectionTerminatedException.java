package Network.Protocol.Exception;

/**
 * Exception to represent case where the connection between two objects is
 * terminated.
 */
public class ConnectionTerminatedException extends NetworkException{
    
    // member variables
    private int port;

    /**
     * Class constructor.
     */
    public ConnectionTerminatedException(int port){
        super("The connection to connector on port : " + port + " was terminated.");
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param port
     * @param cause
     */
    public ConnectionTerminatedException(int port, Exception cause){
        super("The connection to connector on port : " + port + " was terminated.", cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

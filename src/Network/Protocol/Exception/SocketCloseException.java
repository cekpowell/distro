package Network.Protocol.Exception;

/**
 * Exception for case where Connection is not able to close the Socket.
 */
public class SocketCloseException extends NetworkException{
    
    // member variables
    private int port;

    /**
     * Class constructor.
     * 
     * @param port The port the connection was linked to.
     */
    public SocketCloseException(int port){
        super("Unable to close socket for connection to port : " + port);
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param port The port the connection was linked to.
     */
    public SocketCloseException(int port, Exception cause){
        super("Unable to close socket for connection to port : " + port, cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

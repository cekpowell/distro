package Network.Protocol.Exception;

/**
 * Exception for case when a message canot be received.
 */
public class MessageReceivedException extends NetworkException{
    
    // member variables
    private int port;

    /**
     * Class constructor.
     * 
     * @param port
     * @param cause
     */
    public MessageReceivedException(int port, Exception cause){
        super("Unable to receive message from port : " + port, cause);
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }
}

package Network.Protocol.Exception;

/**
 * Exception for case where Connection object was not able to send a message.
 */
public class MessageSendException extends NetworkException{

    // member variables
    private String message;
    private int port;

    /**
     * Class constructor.
     * 
     * @param message
     * @param port
     * @param cause
     */
    public MessageSendException(String message, int port, Exception cause){
        super("Unable to send message '" + message + "' to port : " + port, cause);
        this.message = message;
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param message
     * @param port
     * @param cause
     */
    public MessageSendException(int port, Exception cause){
        super("Unable to send file data to port : " + port, cause);
        this.message = message;
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getMessage(){
        return this.message;
    }

    public int getPort(){
        return this.port;
    }
}

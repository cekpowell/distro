package Network.Protocol.Exception;

/**
 * Exception for the case where a message was not gathered within the timeout period.
 */
public class MessageTimeoutException extends NetworkException{

    // member variables
    
    /**
     * Class constructor.
     */
    public MessageTimeoutException (){
        super("Unable to gather message within timeout period.");

    }

    /**
     * Class constructor.
     */
    public MessageTimeoutException (Exception cause){
        super("Unable to gather message within timeout period.", cause);

    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

}

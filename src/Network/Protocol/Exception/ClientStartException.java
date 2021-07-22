package Network.Protocol.Exception;

/**
 * Exception for case where Client could not be started.
 */
public class ClientStartException extends NetworkException{

    // member variables

    public ClientStartException(Exception cause){
        super("Unable to start Client.", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
}

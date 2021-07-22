package Network.Protocol.Exception;

/**
 * Exception for case where Client could not be setup.
 */
public class ClientSetupException extends NetworkException{

    // member variables

    public ClientSetupException(Exception cause){
        super("Unable to setup Client.", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////
    
}

package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for case when Client input request cannot be gathered.
 */
public class ClientInputRequestReadException extends NetworkException{
    
    /**
     * Class constructor.
     */
    public ClientInputRequestReadException(Exception cause){
        super("Unable to gather input request for Client.", cause);
    }
}

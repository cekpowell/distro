package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for case when provided Client request is null.
 */
public class NullClientInputRequestException extends NetworkException{

    /**
     * Class constructor
     */
    public NullClientInputRequestException(){
        super("Null client request (client request must be non-null.");
    }
}

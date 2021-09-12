package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where an request could not be handeled as the 
 * request handler is disabled.
 */
public class RequestHandlerDisabledException extends NetworkException{
    
    /**
     * Class constructor.
     */
    public RequestHandlerDisabledException(){
        super("The request handler is disabled.");
    }
}

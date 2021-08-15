package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An Exception for the case where there are no valid Dstores for a LOAD operation.
 */
public class NoValidDstoresException extends NetworkException{

    // member variables

    /**
     * Class constructor.
     */
    public NoValidDstoresException(){
        super("No Dstores able to serve the request (exhausted all possible Dstores).");
    }

    /**
     * Class constructor.
     */
    public NoValidDstoresException(Exception cause){
        super("No Dstores able to serve the request (exhausted all possible Dstores).", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

}

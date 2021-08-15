package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where there are not enough Dstores in the system.
 */
public class NotEnoughDstoresException extends NetworkException{
    
    // member variables

    /**
     * Class constructor.
     */
    public NotEnoughDstoresException(){
        super("Not enough Dstores are present in the system.");

    }

    /**
     * Class constructor.
     */
    public NotEnoughDstoresException(Exception cause){
        super("Not enough Dstores are present in the system.", cause);

    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

}

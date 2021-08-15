package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where an operation did not compelte within the timmeout period.
 */
public class OperationTimeoutException extends NetworkException{

    // member variables

    /**
     * Class constructor.
     */
    public OperationTimeoutException (){
        super("A timeout occured waiting for the operation ACKs to arrive from the Dstores.");
    }

    /**
     * Class constructor.
     */
    public OperationTimeoutException (Exception cause){
        super("A timeout occured waiting for the operation ACKs to arrive from the Dstores.", cause);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

}
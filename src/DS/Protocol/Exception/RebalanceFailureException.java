package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for the case where a rebalance operation failed.
 */
public class RebalanceFailureException extends NetworkException{
    
    /**
     * Class constructor.
     */
    public RebalanceFailureException (){
        super("Unable to carry out rebalance.");
    }

    /**
     * Class constructor.
     * 
     * @param cause The cause for the failure.
     */
    public RebalanceFailureException (Exception cause){
        super("Unable to carry out rebalance.", cause);
    }
}

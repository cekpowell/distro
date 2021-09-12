package DS.Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for the case where a rebalance operation cannot be carried out
 * as there is already one in progress.
 */
public class RebalanceAlreadyInProgressException extends NetworkException{
    
    /**
     * Class constructor.
     */
    public RebalanceAlreadyInProgressException(){
        super("There is already a rebalance in progress.");
    }
}

package DS.Protocol.Exception;

import DS.Controller.Index.State.OperationState;
import DS.Controller.Index.State.RebalanceState;
import Network.Protocol.Exception.NetworkException;

/**
 * Exception for the case where a timeout occurs within the network.
 */
public class NetworkTimeoutException extends NetworkException{

    ///////////////////////
    // OPERATION TIMEOUT //
    ///////////////////////

    /**
     * Class constructor.
     * 
     * For instance where timeout occurs during an operation.
     * 
     * @param filename The name of the file involved in the operation.
     * @param expectedState The state the file was expected to reach.
     */
    public NetworkTimeoutException(String filename, OperationState expectedState){
        super("A timeout occured waiting for the file : " + filename + " to reach the state : " + expectedState.toString());
    }

    //////////////////////////
    // SYSTEM STATE TIMEOUT //
    //////////////////////////

    /**
     * Class constructor.
     * 
     * For instace where timeout occurs waiting for the system to reach a 
     * state.
     * 
     * @param expectedState The state the system was expected to reach.
     */
    public NetworkTimeoutException(OperationState expectedState){
        super("A timeout occured while waiting for the system to reach the state : " + expectedState.toString());
    }

    ///////////////////////
    // REBALANCE TIMEOUT //
    ///////////////////////

    /**
     * Class constructor. 
     * 
     * For instance where the timeout occurs during a rebalance.
     * 
     * @param expectedState The rebalance state that was not reached within
     * the ttimeout period.
     */
    public NetworkTimeoutException(RebalanceState expectedState){
        super("A timeout occured waiting for the system to reach the rebalance state : " + expectedState.toString());
    }
}

package DS.Protocol.Event.Rebalance;

import Network.Protocol.Event.NetworkEvent;

/**
 * Event for the case where a Rebalance is carried out.
 */
public class RebalanceCompleteEvent extends NetworkEvent{

    /**
     * Class constructor.
     * 
     * @param rebalancedSystem The system following the rebalance.
     */
    public RebalanceCompleteEvent(){
        super("Rebalance completed.");
    }
}
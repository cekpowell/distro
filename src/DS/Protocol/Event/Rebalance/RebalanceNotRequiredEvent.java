package DS.Protocol.Event.Rebalance;

import Network.Protocol.Event.NetworkEvent;

/**
 * Event for the case where a system rebalance is not required because the
 * file distribution is already balanced.
 */
public class RebalanceNotRequiredEvent extends NetworkEvent{
    
    /**
     * Class constructor.
     */
    public RebalanceNotRequiredEvent(){
        super("Rebalance not required - system is already balanced.");
    }
}

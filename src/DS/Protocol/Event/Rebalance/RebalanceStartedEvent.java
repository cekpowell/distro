package DS.Protocol.Event.Rebalance;

import Network.Protocol.Event.NetworkEvent;

/**
 * Event for the case where a Rebalance is started.
 */
public class RebalanceStartedEvent extends NetworkEvent{

    /**
     * Class constructor.
     */
    public RebalanceStartedEvent(){
        super("Rebalance started.");
    }
}
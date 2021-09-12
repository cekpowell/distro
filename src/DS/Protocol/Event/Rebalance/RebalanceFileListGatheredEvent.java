package DS.Protocol.Event.Rebalance;

import Network.Protocol.Event.NetworkEvent;

/**
 * Event for case where the file list has been gathered from the Dstores
 * during a rebalance.
 */
public class RebalanceFileListGatheredEvent extends NetworkEvent{

    /**
     * Class constructor.
     * 
     */
    public RebalanceFileListGatheredEvent(){
        super("File list gathered from all Dstores for Rebalance.");
    }
}

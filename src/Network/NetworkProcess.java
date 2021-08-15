package Network;

import Network.Protocol.Event.NetworkEvent;
import Network.Protocol.Exception.NetworkException;

/**
 * Reprsents a process that runs on the network (e.g., Client, Server).
 */
public interface NetworkProcess {
    
    /**
     * Starts the network process.
     * 
     * @throws NetworkStartException If the network process could not be started.
     */
    public abstract void start() throws NetworkException;

    /**
     * Sets up the network process.
     * 
     * @throws NetworkException If the network process could not be setup.
     */
    public abstract void setup() throws NetworkException;


    /**
     * Handles an event.
     * 
     * @param event The NetworkEvent that has occured.
     */
    public abstract void handleEvent(NetworkEvent event);

    /**
     * Handles an error.
     * 
     * @param error The NetworkException that has occured.
     */
    public abstract void handleError(NetworkException error);
}

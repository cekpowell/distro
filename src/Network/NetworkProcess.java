package Network;

import Network.Protocol.Exception.NetworkException;

/**
 * Reprsents a process that runs on the network (e.g., Client, Server).
 */
public interface NetworkProcess {

    /**
     * Sets up the network process.
     * 
     * @throws NetworkException If the network process could not be setup.
     */
    public abstract void setup() throws NetworkException;
    
    /**
     * Starts the network process.
     * 
     * @throws NetworkStartException If the network process could not be started.
     */
    public abstract void start() throws NetworkException;


    /**
     * Handles an event.
     * 
     * @param event The NetworkEvent that has occured.
     */
    public abstract void handleEvent(String event);

    /**
     * Handles an error.
     * 
     * @param error The NetworkException that has occured.
     */
    public abstract void handleError(NetworkException error);
}

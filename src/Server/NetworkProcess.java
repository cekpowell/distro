package Server;

/**
 * Reprsents a process that runs on the network.
 */
public interface NetworkProcess {
    
    /**
     * Starts the network process.
     * 
     * @throws Exception if the network process could not be started.
     */
    public abstract void start() throws Exception;
}

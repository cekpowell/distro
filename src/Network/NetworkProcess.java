package Network;

/**
 * Reprsents a process that runs on the network (e.g., Client, Server).
 */
public interface NetworkProcess {
    
    /**
     * Starts the network process.
     * 
     * @throws Exception if the network process could not be started.
     */
    public abstract void start() throws Exception;
}

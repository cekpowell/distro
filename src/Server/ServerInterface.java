package Server;

/**
 * 
 */
public abstract class ServerInterface implements NetworkInterface{

    /**
     * Starts the Server.
     */
    public void startServer(Server server){
        try{
            // trying to start the server
            server.start();

            // Controller successfully started
        }
        catch(Exception e){
            // handling error if server could not be started
            this.logError(e.getMessage());
        }
    }

    /**
     * 
     * 
     * @throws Exception
     */
    public abstract void createLogger() throws Exception;
}
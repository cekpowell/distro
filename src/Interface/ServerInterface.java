package Interface;

import Server.Server;

/**
 * 
 */
public abstract class ServerInterface implements NetworkInterface{

    /**
     * Tries to start the underlying Server.
     * 
     * Passes an error to the interface's error logger if the start was unsuccessful.
     */
    public void startServer(Server server){
        try{
            // trying to start the server
            server.start();

            // Controller successfully started
        }
        catch(Exception e){
            // handling error if server could not be started
            this.handleError(e.getMessage());
        }
    }

    /**
     * 
     * 
     * @throws Exception
     */
    public abstract void createLogger() throws Exception;
}
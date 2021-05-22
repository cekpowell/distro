package Interface;

import Server.Server;

/**
 * Abstract class to represnt the interface betwen a Server and the user.
 * 
 * The class implements a 'startServer' method which tries to start the provided 
 * server. If the server could not be started, an error is passed onto the underlying
 * interface.
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
     * Creates the logger for the Server.
     * 
     * Only neededd becausee of DstoreLogger and ControllerLogger.
     * 
     * @throws Exception Thrown if the logger could not be created.
     */
    public abstract void createLogger() throws Exception;
}
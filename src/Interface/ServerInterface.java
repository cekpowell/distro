package Interface;

import Server.Server;

/**
 * Abstract class to represnt the interface betwen a Server and the user.
 * 
 * The class implements a 'startServer' method which tries to start the provided 
 * server. If the server could not be started, an error is passed onto the underlying
 * interface.
 */
public abstract class ServerInterface extends NetworkInterface{

    /**
     * Creates the logger for the Server.
     * 
     * Only neededd becausee of DstoreLogger and ControllerLogger.
     * 
     * @throws Exception Thrown if the logger could not be created.
     */
    public abstract void createLogger() throws Exception;
}
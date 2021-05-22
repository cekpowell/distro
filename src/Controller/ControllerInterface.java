package Controller;

import java.net.Socket;

import Server.*;

/**
 * 
 */
public abstract class ControllerInterface implements ServerInterface{

    /**
     * Starts the Controller.
     */
    public void startController(Controller controller){
        try{
            // trying to start the server
            controller.start();

            // Controller successfully started
        }
        catch(Exception e){
            // handling error if server could not be started
            this.logError(e.getMessage());
        }
    }

    /**
     * Handles the logging of a new Dstore joining the 
     * @param connection The connection between the controller and the Dstore.
     * @param port The port the Dstore listens on.
     */
    public abstract void logDstoreJoined(Socket connection, int port);
}
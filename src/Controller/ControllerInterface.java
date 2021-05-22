package Controller;

import java.net.Socket;

import Interface.ServerInterface;

/**
 * Class needed for the reason that a controller must also log the joining of a 
 * Dstore.
 * 
 * This class will be removed when such logging is no longer required.
 */
public abstract class ControllerInterface extends ServerInterface{

    /**
     * Handles the logging of a new Dstore joining the system. 
     * 
     * @param connection The connection between the controller and the Dstore.
     * @param port The port the Dstore listens on.
     */
    public abstract void logDstoreJoined(Socket connection, int port);
}
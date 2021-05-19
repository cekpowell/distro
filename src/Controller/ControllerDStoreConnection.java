package Controller;

import java.net.Socket;

import Connection.*;

/**
 * 
 */
public class ControllerDStoreConnection extends Connection{

    // member variables
    private Controller controller;
    private int dStoreListenPort;
    
    /**
     * Class constructor.
     * @param controller Controller involved in the connection.
     * @param connection The connection between the Controller and the DStore.
     */
    public ControllerDStoreConnection(Controller controller, int dStoreListenPort, Socket connection){
        super(controller, connection);
        this.controller = controller;
        this.dStoreListenPort = dStoreListenPort;
    }

    /**
     * Method run when thread started.
     */
    public void run(){
        // Nothing to do...     
    }

    /**
     * Getters and setters.
     */
    public int getDStoreListenPort(){
        return this.dStoreListenPort;
    }
}

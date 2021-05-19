package Dstore;

import java.net.Socket;

import Logger.DstoreLogger;
import Logger.Protocol;
import Connection.*;

/**
 * Represents the connection from DStore to Controller.
 */
public class DstoreControllerConnection extends Connection{

    // member variable
    private Dstore dStore;

    /**
     * Class constructor
     * @param dStore
     * @param connection
     */
    public DstoreControllerConnection(Dstore dStore, Socket connection){
        // initialising member variables
        super(dStore, connection);
        this.dStore = dStore;
    }

    /**
     * Method run when thread started
     */
    public void run(){
        // Sending JOIN request to controller
        String message = Protocol.JOIN_TOKEN + " " + this.dStore.getPort();
        this.getTextOut().println(message);
        this.getTextOut().flush(); // closing the stream
        DstoreLogger.getInstance().messageSent(this.getConnection(), message);


        // 2 - start listening for communication from controller
    }

    /**
     * Getters and setters
     */
}

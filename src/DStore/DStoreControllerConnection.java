package DStore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import Logger.DstoreLogger;
import Logger.Protocol;
import Connection.*;

/**
 * 
 */
public class DStoreControllerConnection extends Connection{

    // member variable
    private DStore dStore;

    /**
     * Class constructor
     * @param dStore
     * @param connection
     */
    public DStoreControllerConnection(DStore dStore, Socket connection){
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

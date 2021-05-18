package DStore;

import java.net.Socket;

 /**
  * 
  */
public class DStore {

    // member variables
    int port;
    int cPort;
    double timeout;
    String fileFolder;

    /**
     * Class constructor.
     * @param port
     * @param cPort
     * @param timeout
     * @param fileFolder
     */
    public DStore(int port, int cPort, double timeout, String fileFolder){
        // initializing member variables
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.fileFolder = fileFolder;

        // connecting to the controller
        // TODO

        // waiting for client connection
        this.waitForClientConnection();
    }

    /**
     * Handles incoming communication to the data store from a client.
     */
    public void waitForClientConnection(){
        // TODO
    }
}
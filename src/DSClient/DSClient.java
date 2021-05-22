package DSClient;

import Interface.ClientInterface;
import Server.*;

/**
 * Abstract class to represent a Client within the system.
 * 
 * A Client connects to a Data Store by connecting to a Controller.
 * 
 * The underlying Client object will take in requests and pass them to the 
 * defined handleRequest method.
 * 
 * The handleRequest method will then return the response of the request to the 
 * handleResponse method of the underlying object.
 */
public class DSClient extends Client{

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public DSClient(int cPort, int timeout, ClientInterface clientInterface) {
        // initialising member variables
        super(cPort, timeout, clientInterface);
    }

    /**
     * 
     */
    public void handleResponse(Connection connection, String message){
        /**
         * TODO need to handle case where response requires some action
         * 
         * For now, just logs response to screen.
         */
        this.getClientInterface().logMessageReceived(connection, message);
    }
}
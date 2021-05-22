package DSClient;

import Interface.ClientInterface;
import Server.*;

/**
 * Abstract class to represent a DSClient within the system.
 * 
 * A DSClient connects to a Data Store by connecting to a Controller.
 * 
 * The class will send requests using the underlying Client object, and 
 * handles the responses to these requests with the handleResponse method.
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
     * Handles the given response.
     * 
     * @param connection The socket the response was recieved from.
     * @param message The recieved response.
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
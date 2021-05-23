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
    public void handleInputRequest(String request){
        /**
         * TODO
         * 
         * At the moment this just takes the inupt request, sends it to the server and gets the response.
         * 
         * Will need to handle different input requests differently based on what they tokenize to.
         * 
         * e.g., For Store, send store to controller, get response of which dstores to store to and then send messages
         * to those dstores.
         * 
         * e.g., for list, will just need to send message and get response
         */
        try{
            // sending message to Controller
            this.getServerConnection().sendMessage(request);

            // gathering response
            this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
        }
        catch(Exception e){
            // unable to handle input request

            //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
            this.getClientInterface().handleError("Unable to handle input request : " + request + " sent to Controller on port : " + this.getCPort());
        }
    }
}
package DSClient;

import Interface.NetworkInterface;
import Server.*;
import Token.*;
import Token.TokenType.ListToken;


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
    public DSClient(int cPort, int timeout, NetworkInterface clientInterface) {
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
            try{
                Token requestToken = RequestTokenizer.getToken(request);

                // pattern matching for token type
                if(requestToken instanceof ListToken){
                    this.handleListRequest(request);
                }

                // invalid request or request not expected by Client
                else{
                    this.handleInvalidRequest(request);
                }
            }
            catch(Exception e){
                // unable to handle input request

                //TODO need to test for different types of exception to know where the error occuredd - e.g., SocketTimeoutException, NullPointerException, etc...
                this.getClientInterface().handleError("Unable to handle input request : " + request + " sent to Controller on port : " + this.getCPort());
            }
    }

    /**
     * 
     * @param request
     * @throws Exception
     */
    private void handleListRequest(String request) throws Exception{
        // sending message to Controller
        this.getServerConnection().sendMessage(request);

        // gathering response
        this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    }

    /**
     * 
     * @param request
     * @throws Exception
     */
    private void handleInvalidRequest(String request) throws Exception{
        // sending message to Controller
        this.getServerConnection().sendMessage(request);

        // gathering response
        this.getServerConnection().getMessageWithinTimeout(this.getTimeout());
    }
}
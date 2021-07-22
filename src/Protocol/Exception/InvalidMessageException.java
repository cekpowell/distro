package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * An exception for the case where an invalid message is receieved.
 */
public class InvalidMessageException extends NetworkException {
    
    // member variables
    private String response;
    private int port;

    /**
     * Class constructor.
     * 
     * @param response The invalid response.
     * @param port The port the response was received from.
     */
    public InvalidMessageException (String response, int port){
        super("Invalid message '" + response + "' received from port : " + port);
        this.response = response;
        this.port = port;
    }

    /**
     * Class constructor.
     * 
     * @param response The invalid response.
     * @param port The port the response was received from.
     */
    public InvalidMessageException (String response, int port, Exception cause){
        super("Invalid message '" + response + "' received from port : " + port, cause);
        this.response = response;
        this.port = port;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getResponse(){
        return this.response;
    }

    public int getPort(){
        return this.port;
    }
}

package Protocol.Exception;

import Network.Protocol.Exception.NetworkException;

/**
 * Exception for the case where the Client inputs an invalid request
 */
public class InvalidClientRequestException extends NetworkException{

    // member variables
    private String request;

    /**
     * Class constructor.
     * 
     * @param request The input request the Client entered.
     */
    public InvalidClientRequestException(String request){
        super("The input request " + request + " is invalid.");
        this.request = request;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getRequest(){
        return this.request;
    }
}

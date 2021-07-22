package Network.Protocol.Exception;

/**
 * Exception to represent the case where a network process can't handle
 * a given request.
 */
public class RequestHandlingException extends NetworkException{

    // member variables
    private String request;

    /**
     * Class constructor.
     */
    public RequestHandlingException(String request, Exception cause){
        super("Unable to handle request '" + request + "'.", cause);
        this.request = request;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getRequest(){
        return this.request;
    }
}

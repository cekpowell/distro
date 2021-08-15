package Network.Protocol.Exception;

/**
 * Represents a NetworkException that has been handeled by a network process.
 */
public class HandeledNetworkException {

    // member variables
    private NetworkException exception;
    
    /**
     * Class Constructor.
     * 
     * @param exception The NetworkException that has been handeled.
     */
    public HandeledNetworkException(NetworkException exception){
        this.exception = exception;
    }
    /**
     * Converts the exception to a string.
     */
    public String toString(){
        // returning string
        return this.exception.toString();
    }

    /**
     * Gets the exception that has been handeled.
     * 
     * @return The exception that has been handeled.
     */
    public NetworkException getException(){
        return this.exception;
    }
}

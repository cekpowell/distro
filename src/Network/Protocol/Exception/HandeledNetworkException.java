package Network.Protocol.Exception;

public class HandeledNetworkException {

    // member variables
    private NetworkException exception;
    
    /**
     * Class Constructor.
     * 
     * @param message The error message.
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

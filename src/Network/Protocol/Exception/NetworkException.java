package Network.Protocol.Exception;

/**
 * Represents the type of error that can occur within the system.
 */
public abstract class NetworkException extends Exception{
    
    /**
     * Class Constructor.
     * 
     * @param message The error message.
     */
    public NetworkException(String message){
        super(message);
    }

    /**
     * Class constructor.
     * 
     * @param message The error message.
     * @param cause The Exception that caused the error.
     */
    public NetworkException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Converts the exception to a string.
     */
    public String toString(){
        // builder
        String string = "";

        // Error
        string += "*ERROR* " + this.getMessage();

        // Cause
        string += this.getCauseTrace();

        // returning string
        return string;
    }

    /**
     * Gets the cause trace for the error.
     * 
     * @return The cause trace for the error as a string.
     */
    private String getCauseTrace(){

        // cause trace string
        String causeTrace = "";

        // current cause
        Throwable currentCause = this.getCause();

        while(currentCause != null){
            causeTrace += "\n  |-CAUSE : " + currentCause.getMessage() +"";

            currentCause = currentCause.getCause();
        }

        return causeTrace;
    }
}

package Network.Protocol.Event;

/**
 * Represents an event that can occur within the system.
 */
public class NetworkEvent {

    // member variables
    private String message;
    
    /**
     * Class constructor.
     * 
     * @param message The string message associated with the event.
     */
    public NetworkEvent(String message){
        this.message = message;
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public String getMessage(){
        return this.message;
    }

    public String toString(){
        return ("#EVENT# " + this.message);
    }
}

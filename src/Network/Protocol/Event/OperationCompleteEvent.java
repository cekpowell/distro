package Network.Protocol.Event;

/**
 * Event that represents the completion of an operation within the system.
 */
public abstract class OperationCompleteEvent extends NetworkEvent{

    /**
     * Class constroctor.
     * 
     * @param message The String message associated with the event.
     */
    public OperationCompleteEvent(String message){
        super(message);
    }
}

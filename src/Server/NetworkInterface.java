package Server;

import java.net.Socket;

/**
 * Represents the type of object that sends and recieves messages, and needs to log 
 * these messages.
 * 
 * Such an object must implement the methods of logMessageSend, logMessageRecieved,
 * LogEvent and LogError.
 * 
 * How these events are logged is up to the class that implements them.
 */
public interface NetworkInterface {

    /**
     * Handles the logging of a message being sent.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public abstract void logMessageSent(Socket connection, String message);

    /**
     * Handles the logging of a message being recieved.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public abstract void logMessageReceived(Socket connection, String message);

    /**
     * Handles the logging of an event.
     * 
     * // TODO instead of the event being passed as a string, make it an Event object, with different types.
     * 
     * @param event The event to be logged.
     */
    public abstract void logEvent(String event);

    /**
     * Handles the logging of an error.
     * 
     * // TODO Instead of the error being passed as a string, create Exception objects for all of the types of exception.
     * 
     * @param error The error to be logged.
     */
    public abstract void logError(String error);
}

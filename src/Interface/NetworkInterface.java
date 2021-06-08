package Interface;

import java.net.Socket;

import Network.NetworkProcess;

/**
 * Represents the type of object that acts as an interface between the user
 * and a component of the network.
 * 
 * Such an object must implement the methods of logMessageSend, logMessageRecieved,
 * LogEvent and handleError.
 * 
 * How these events are logged is up the specific interface that implements this.
 */
public abstract class NetworkInterface {

    /**
     * Tries to start the underlying Server.
     * 
     * Passes an error to the interface's error logger if the start was unsuccessful.
     */
    public void startNetworkProcess(NetworkProcess process){
        try{
            // trying to start the server
            process.start();

            // Controller successfully started
        }
        catch(Exception e){
            // handling error if server could not be started
            this.handleError(e.getMessage());
        }
    }

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
     * Handles an error.
     * 
     * // TODO Instead of the error being passed as a string, create Exception objects for all of the types of exception.
     * 
     * @param error The error to be logged.
     */
    public abstract void handleError(String error);
}

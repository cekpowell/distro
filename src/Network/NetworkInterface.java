package Network;

import java.net.Socket;

import Network.Protocol.Exception.HandeledNetworkException;
import Network.Protocol.Exception.NetworkException;

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

    // member variables
    private NetworkProcess networkProcess;

    /**
     * Starts the network process.
     * 
     * @param networkProcess The network process associated with the interface, and being
     * started.
     */
    public void startNetworkProcess(NetworkProcess networkProcess){
        try{
            this.networkProcess = networkProcess;
            
            // trying to start the server
            networkProcess.start();
        }
        catch(NetworkException e){
            // handling case where the process couldnt be started
            networkProcess.handleError(e);
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
     * @param event The event to be logged.
     */
    public abstract void logEvent(String event);

    /**
     * Handles an error.
     * 
     * @param error The error to be handeled.
     */
    public abstract void logError(HandeledNetworkException error);

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public NetworkProcess getNetworkProcess(){
        return this.networkProcess;
    }
}

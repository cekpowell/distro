package DS.Dstore;

import java.net.Socket;

import DS.Protocol.Exception.*;
import Network.NetworkInterface;
import Network.Protocol.Event.HandeledNetworkEvent;
import Network.Protocol.Exception.HandeledNetworkException;
import Network.Protocol.Exception.ServerStartException;

/**
 * Implementation of ServerInterface that provides an interface for a 
 * data store Dstore through the terminal.
 * 
 * Messages are logged onto the terminal through stdout.
 */
public class DstoreTerminal extends NetworkInterface{

    int port; // TODO only needed to create the logger - can remove when logger is removed.
    Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param folderPath The folder where the DStore will store files.
     */
    public DstoreTerminal(int port, int cPort, int timeout, String folderPath){
        this.port = port;
        this.dstore = new Dstore(port, cPort, timeout, folderPath, this);

        // starting the Dstore
        this.startNetworkProcess(this.dstore);
    }

    /////////////
    // LOGGING //
    /////////////

    /**
     * Handles the logging of a message being sent.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageSent(Socket connection, String message){
        System.out.println("[" + connection.getLocalPort() + " -> " + connection.getPort() + "] " + message);
    }

    /**
     * Handles the logging of a message being recieved.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageReceived(Socket connection, String message){
        System.out.println("[" + connection.getLocalPort() + " <- " + connection.getPort() + "] " + message);
    }

    /**
     * Handles the logging of an event.
     * 
     * @param event The event to be logged.
     */
    public void logEvent(HandeledNetworkEvent event){
        System.out.println(event.toString());
    }
    
    /**
     * Handles the logging of an error and it's cause.
     * 
     * @param error The error to be logged.
     */
    public void logError(HandeledNetworkException error){
        // logging error to terminal
        System.out.println(error.toString());

        // HANDLING ERROR //

        // Controller Disconnect
        if(error.getException() instanceof ControllerDisconnectException){
            // closing system
            System.exit(0);
        }

        // Server Start Exception
        else if(error.getException() instanceof ServerStartException){
            // closing the system
            System.exit(0);
        }
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new DStore instance using the command line parammeters.
     * 
     * @param args Parameters for the new DStore.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int port = Integer.parseInt(args[0]);
            int cPort = Integer.parseInt(args[1]);
            int timeout = Integer.parseInt(args[2]);
            String fileFolder = args[3];

            // Creating new DStore instance
            DstoreTerminal dataStore = new DstoreTerminal(port, cPort, timeout, fileFolder);
        }
        catch(Exception e){
            System.out.println("Unable to create DStore." + e.toString());
        }
    }
}
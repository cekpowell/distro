package DS.Controller; 

import java.net.Socket;

import DS.Protocol.Exception.LoggerCreationException;
import Network.NetworkInterface;
import Network.Protocol.Event.HandeledNetworkEvent;
import Network.Protocol.Exception.*;
import Network.Server.Server.ServerType;

/**
 * Implementation of ServerInterface that provides an interface for a 
 * data store Controller through the terminal.
 * 
 * Messages are logged onto the terminal through stdout.
 */
public class ControllerTerminal extends NetworkInterface{

    // member variables
    Controller controller;

    /**
     * Class constructor.
     * 
     * @param port The port the controller should listen on.
     * @param r The number of data stores to replicate files across.
     * @param timeout The timeout length for communication.
     * @param rebalancePeriod The rebalance period.
     */
    public ControllerTerminal(int port, int r, int timeout, int rebalancePeriod){
        this.controller = new Controller(port, r, timeout, rebalancePeriod, this);

        // starting Controller
        this.startNetworkProcess(this.controller); // start  it on seperate thread
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
     * Handles the logging of an error.
     * 
     * @param error The error to be logged.
     */
    public void logError(HandeledNetworkException error){
        // logging error to terminal
        System.out.println(error.toString());

        // HANDLING ERROR //

        // Server Start Exception
        if(error.getException() instanceof ServerStartException){
            // closing the system
            System.exit(0);
        }
    }
    

    /////////////////
    // MAIN METHOD //
    /////////////////

    
    /**
     * Main method - instantiates a new Controller instance using the command line parammeters.
     * 
     * @param args Parameters for the new Controller.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int cPort = Integer.parseInt(args[0]);
            int r = Integer.parseInt(args[1]);
            int timeout = Integer.parseInt(args[2]);
            int rebalancePeriod = Integer.parseInt(args[3]);

            // Creating new DStore instance
            ControllerTerminal controller = new ControllerTerminal(cPort, r, timeout, rebalancePeriod);
        }
        catch(Exception e){
            System.out.println("Unable to create Controller.");
        }
    }
}
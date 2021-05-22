package Controller; 

import java.net.Socket;

import Logger.*;

/**
 * Implementation of ServerInterface that provides an interface for a 
 * data store Controller through the terminal.
 * 
 * Messages are logged onto the terminal through stdout.
 */
public class ControllerTerminal extends ControllerInterface{

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
        this.startServer(this.controller); // start  it on seperate thread
    }

    /**
     * Method run when server is started.
     * 
     * Needed so that the logger can be set up.
     * 
     * Dont need the method for future implementations as won't need to set up the logger like this.
     */
    public void createLogger() throws Exception{
        // initialising Logger //
        ControllerLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY);
    }

    /////////////
    // LOGGING //
    /////////////

    /**
     * Handles the logging of a new Dstore joining the 
     * @param connection
     * @param port
     */
    public void logDstoreJoined(Socket connection, int port){
        ControllerLogger.getInstance().dstoreJoined(connection, port);
    }

    /**
     * Handles the logging of a message being sent.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageSent(Socket connection, String message){
        ControllerLogger.getInstance().messageSent(connection, message);
    }

    /**
     * Handles the logging of a message being recieved.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageReceived(Socket connection, String message){
        ControllerLogger.getInstance().messageReceived(connection, message);
    }

    /**
     * Handles the logging of an event.
     * 
     * 
     * @param event The event to be logged.
     */
    public void logEvent(String event){
        System.out.println("#EVENT# " + event);
    }

    /**
     * Handles the logging of an error.
     * 
     * @param error The error to be logged.
     */
    public void handleError(String error){
        System.out.println("*ERROR* " + error);
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
package Dstore;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;

import Logger.*;
import Server.*;

 /**
  * Individual data store unit within the system. 
  *
  * Connects to a Controller to join a data store and servers requests from Clients.
  */
public class DstoreTerminal extends DstoreInterface{

    int port; // TODO only needed to create the logger - can remove when logger is removed.
    Dstore dstore;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     */
    public DstoreTerminal(int port, int cPort, int timeout, String folderPath){
        this.port = port;
        this.dstore = new Dstore(port, cPort, timeout, folderPath, this);

        // starting the Dstore
        this.startDstore(this.dstore);
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
        DstoreLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY, this.port);
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
        DstoreLogger.getInstance().messageSent(connection, message);
    }

    /**
     * Handles the logging of a message being recieved.
     * 
     * @param connection The socket between the sender and reciever.
     * @param message The message to be logged.
     */
    public void logMessageReceived(Socket connection, String message){
        DstoreLogger.getInstance().messageReceived(connection, message);
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
    public void logError(String error){
        System.out.println("*ERROR* " + error);
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
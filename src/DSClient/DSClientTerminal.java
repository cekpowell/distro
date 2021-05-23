package DSClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Interface.NetworkInterface;

/**
 * Implementation of a ClientInterface that gathers user requests from the terminal.
 * 
 * Creates a DSClient object, that connects to the Controller when started.
 * 
 * The interface takes requests from the user on stdin and passes them to the DSClient
 * object to be sent to the Controller.
 * 
 * The interface logs the response to requests on stdout.
 */
public class DSClientTerminal extends NetworkInterface{

    // member variables
    private DSClient client;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public DSClientTerminal(int cPort, int timeout) {
        // initialising member variables
        this.client = new DSClient(cPort, timeout, this);

        // connecting to network
        this.startNetworkProcess(this.client);

        // waiting for user input
        this.waitForInput();
    }

    /**
     * Waits for user to input a request into the terminal.
     */
    public void waitForInput(){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Wait for input 
            while(true){
                // FORMATTING
                System.out.print(">");

                // reading in request
                String request = reader.readLine();

                // making sure client request is non-null
                if(request.equals("")){
                    this.handleError("Request cannot be null.");
                }
                else{
                    // sending request to controller
                    this.client.handleInputRequest(request);
                }
            }
        }
        catch(Exception e){
            this.handleError("Unable to gather user input for Client.");
        }
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

        // Formatting
        System.out.println();
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
        // logging error to terminal
        System.out.println("*ERROR* " + error);

        // checking if error was controller disconnected
        if(error.equals("Lost connection to Server on port : " + this.client.getCPort())){
            System.exit(0);
        }
        else if(error.equals("Unable to connect Client to Server on port : " + this.client.getCPort())){
            System.exit(0);
        }
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new Client instance using the command line parammeters.
     * @param args Parameters for the new Client.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int cPort = Integer.parseInt(args[0]);
            int timeout = Integer.parseInt(args[1]);

            // Creating new DStore instance
            DSClientTerminal client = new DSClientTerminal(cPort, timeout);
        }
        catch(Exception e){
            System.out.println("Unable to create Client.");
        }
    }
}
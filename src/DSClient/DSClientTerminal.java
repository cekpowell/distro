package DSClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import Network.NetworkInterface;
import Network.Protocol.Exception.ClientStartException;
import Network.Protocol.Exception.HandeledNetworkException;
import Network.Protocol.Exception.RequestHandlingException;
import Protocol.Exception.ClientInputRequestReadException;
import Protocol.Exception.ControllerDisconnectException;
import Protocol.Exception.NullClientInputRequestException;

/**
 * Implementation of a ClientInterface that uses the terminal as an interface
 * betweeen the Client and the user.
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
    public DSClient client;

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
                System.out.println();
                System.out.print(">");

                // reading in request
                String request = reader.readLine();

                // making sure client request is non-null
                if(request.equals("")){
                    this.client.handleError(new RequestHandlingException("", new NullClientInputRequestException()));
                }
                else{
                    // sending request to controller
                    this.client.handleInputRequest(request);
                }
            }
        }
        catch(Exception e){
            this.client.handleError(new ClientInputRequestReadException(e));
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
    }

    /**
     * Handles the logging of an event.
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
    public void logError(HandeledNetworkException error){
        // logging error to terminal
        System.out.println(error.toString());

        // HANDLING SPECIFIC CASES //

        // Controller disconnected
        if(error.getException() instanceof ControllerDisconnectException){
            System.exit(0);
        }
        // Client Start Exception
        else if(error.getException() instanceof ClientStartException){
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

            // Creating new Client instance
            new DSClientTerminal(cPort, timeout);
        }
        catch(Exception e){
            System.out.println("Unable to create Client.");
        }
    }
}
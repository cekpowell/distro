package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Implementation of a Client that gathers user requests from the terminal.
 * 
 * Connects to a Data Store by connecting to a Controller.
 * 
 * Takes requests from the user on stdin.
 * 
 * Sends requests to Controller, processes the response and outputs the result
 * to stdout.
 */
public class ClientTerminal extends ClientInterface{

    // member variables
    private Client client;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public ClientTerminal(int cPort, int timeout) {
        // initialising member variables
        this.client = new Client(cPort, timeout, this);

        // connecting to network
        this.startClient(this.client);

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
                    this.logError("Request cannot be null.");
                }
                else{
                    // sending request to controller
                    this.client.sendRequest(request);
                }
            }
        }
        catch(Exception e){
            this.logError("Unable to gather user input for Client.");
        }
    }

    /**
     * Handles a request response.
     * 
     * @param response The tokenized response from a request.
     */
    public void handleResponse(Socket connection, String response){
        // logging response
        this.logMessageReceived(connection, response);
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
    public void logError(String error){
        System.out.println("*ERROR* " + error);
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
            ClientTerminal client = new ClientTerminal(cPort, timeout);
        }
        catch(Exception e){
            System.out.println("Unable to create Client.");
        }
    }
}
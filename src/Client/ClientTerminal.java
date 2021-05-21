package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Logger.MyLogger;
import Token.Token;
import Token.TokenType.ListFilesToken;

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
public class ClientTerminal extends Client{

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public ClientTerminal(int cPort, int timeout) {
        // initialising member variables
        super(cPort, timeout);
    }

    /**
     * Method run to start the Client when is has been set up.
     */
    public void start(){
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

                // sending request to controller
                this.sendRequest(request);
            }
        }
        catch(Exception e){
            MyLogger.logError("Unable to gather user input for Client.");
        }
    }

    /**
     * Handles a request rsponse.
     * @param response The tokenized response from a request.
     */
    public void handleResponse(Token response){

        ///////////////////////
        // Handling Response //
        ///////////////////////

        // FORMATTING
        MyLogger.logEvent("Response recieved : ");
        System.out.print("\t");

        if(response instanceof ListFilesToken){
            // gathering filenames
            ListFilesToken listFilesToken = (ListFilesToken) response;
            ArrayList<String> filenames = listFilesToken.filenames;

            // forming message
            String message = String.join("\n\t", filenames);

            // outputting message
            System.out.println(message);
        }

        // Unexpected response //
        else{
            System.out.println("Invalid response recieved.");
        }

        // TODO Handle all other types of response


        // FORMATTING
        System.out.println();
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
            MyLogger.logError("Unable to create Client.");
        }
    }
}
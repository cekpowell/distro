package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import Logger.MyLogger;
import Token.RequestTokenizer;
import Token.Token;
import Token.TokenType.ListFilesToken;

/**
 * Client. Connects to a Data Store by connecting to the stores controller.
 * 
 * Takes requests from the user on stdin.
 * 
 * Sends requests to Controller, processes the response and outputs the result
 * to stdout.
 */
public class Client {

    // member variables
    int cPort;
    int timeout;
    Socket controllerConnection;

    /**
     * Class Constructor.
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public Client(int cPort, int timeout) {
        // initialising member variables
        this.cPort = cPort;
        this.timeout = timeout;
        
        // starting the Client
        this.setupAndRun();
    }

    /**
     * Sets up and starts the Client for the system.
     * 
     * Trys to connect to the Controller.
     * 
     * Waits for user input if successful, closes otherwise.
     */
    public void setupAndRun(){
        try{
            // connecting to controller
            this.connectToController();

            // Connection successful ...

            // waiting for user input
            this.waitForInput();
        }
        catch(Exception e){
            MyLogger.logError("Unable to connect Client to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     */
    public void connectToController() throws Exception{
        this.controllerConnection = new Socket(InetAddress.getLocalHost(), this.cPort);
        this.controllerConnection.setSoTimeout(timeout);
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

                // handling request
                this.handleRequest(request);
            }
        }
        catch(Exception e){
            MyLogger.logError("Unable to gather user input for Client.");
        }
    }

    /**
     * Handles a user's input request.
     */
    public void handleRequest(String request){
        // Sending request to controller
        try{
            PrintWriter out = new PrintWriter (new OutputStreamWriter(this.controllerConnection.getOutputStream()));
            out.println(request);
            out.flush(); // closing the stream

            // logging request
            MyLogger.logEvent("Request : \"" + request + "\" sent to Controller on port : " + this.cPort);

            // FORMATTING
            System.out.println("Waiting for response...");

            // gathering response from controller 
            BufferedReader in = new BufferedReader(new InputStreamReader(this.controllerConnection.getInputStream()));
            Token response = RequestTokenizer.getToken(in.readLine());

            // handling response within timeout
            this.handleResponse(response);
        }
        catch(SocketTimeoutException e){
            MyLogger.logError("Timeout occurred on request : \"" + request + "\" to Controller on port : " + this.cPort);

            // FORMATTING
            System.out.println();
        }
        catch(Exception e){
            MyLogger.logError("Unable to handle request : \"" + request + "\" to Controller on port : " + this.cPort);
            
            // FORMATTING
            System.out.println();
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
            Client client = new Client(cPort, timeout);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create Client.");
        }
    }
}
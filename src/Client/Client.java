package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import Logger.MyLogger;

/**
 * 
 */
public class Client {

    // member variables
    int cPort;
    double timeout;
    Socket controllerConnection;

    /**
     * Class Constructor.
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public Client(int cPort, double timeout) {
        // initialising member variables
        this.cPort = cPort;
        this.timeout = timeout;

        // connecting the client to the controller
        try{
            // creating socket for communication
            this.controllerConnection = new Socket(InetAddress.getLocalHost(), this.cPort);
        }
        catch(Exception e){
            MyLogger.logError("Unable to connect Client to controller on port : " + this.cPort);
        }

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
        }
        catch(Exception e){
            MyLogger.logError("Unable to send request : " + request + " to Controller on port : " + this.cPort);
            MyLogger.logError(e.getMessage());
        }


        // gathering response from controller 
        // TODO
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
            double timeout = Double.parseDouble(args[1]);

            // Creating new DStore instance
            Client client = new Client(cPort, timeout);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create DStore.");
        }
    }
}
package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import Logger.MyLogger;
import Token.RequestTokenizer;
import Token.Token;

/**
 * Abstract class to represent a Client within the system.
 * 
 * A Client connects to a Data Store by connecting to a Controller.
 * 
 * The underlying Client object will take in requests and pass them to the 
 * defined handleRequest method.
 * 
 * The handleRequest method will then return the response of the request to the 
 * handleResponse method of the underlying object.
 */
public abstract class Client {

    // member variables
    int cPort;
    int timeout;
    Socket controllerConnection;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public Client(int cPort, int timeout) {
        // initialising member variables
        this.cPort = cPort;
        this.timeout = timeout;

        // starting the Client
        this.setupAndStart();
    }

    /**
     * Sets up and starts the Client for the system.
     * 
     * Trys to connect to the Controller.
     * 
     * Waits for user input if successful, closes otherwise.
     */
    public void setupAndStart(){
        try{
            // connecting to controller
            this.connectToController();

            // Connection successful ...

            // starting the client
            this.start();
        }
        catch(Exception e){
            MyLogger.logError("Unable to connect Client to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     */
    private void connectToController() throws Exception{
        this.controllerConnection = new Socket(InetAddress.getLocalHost(), this.cPort);
        this.controllerConnection.setSoTimeout(timeout);
    }

    /**
     * Method started when the Client successfully connects to the Controller.
     * 
     * Starts the Client listening for input.
     */
    public abstract void start();

    /**
     * Send's a user's input request to the Controller the Client is connected to.
     */
    public void sendRequest(String request){
        // Sending request to controller
        try{
            PrintWriter out = new PrintWriter (new OutputStreamWriter(this.controllerConnection.getOutputStream()));
            out.println(request);
            out.flush(); // closing the stream

            // logging request
            MyLogger.logEvent("Request : \"" + request + "\" sent to Controller on port : " + this.cPort);

            // gathering response
            this.gatherResponse(request);
        }
        catch(Exception e){
            MyLogger.logError("Unable to send request : \"" + request + "\" to Controller on port : " + this.cPort);
            
            // FORMATTING
            System.out.println();
        }
    }

    /**
     * Gathers a response for a request.
     * 
     * Reads from the InputReader of the Controller connection for the response.
     * 
     * @param request The request the response is being gathered for.
     */
    public void gatherResponse(String request){
        try{
            // FORMATTING
            System.out.println("Waiting for response...");

            // gathering response from controller 
            BufferedReader in = new BufferedReader(new InputStreamReader(this.controllerConnection.getInputStream()));
            Token response = RequestTokenizer.getToken(in.readLine());

            // response gathered within timeout...

            // handling response
            this.handleResponse(response);
        }
        catch(SocketTimeoutException e){
            MyLogger.logError("Timeout occurred on request : \"" + request + "\" to Controller on port : " + this.cPort);

            // FORMATTING
            System.out.println();
        }
        catch(Exception e){
            MyLogger.logError("Unable to recieve response for request : \"" + request + "\" from Controller on port : " + this.cPort + " (Controller likley disconnected).");
            
            // FORMATTING
            System.out.println();
        }
    }

    /**
     * Handles a request response.
     * 
     * @param response The tokenized response from a request.
     */
    public abstract void handleResponse(Token response);
}
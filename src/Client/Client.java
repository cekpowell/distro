package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import Server.Connection;
import Server.HeartbeatConnection;

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
public class Client {

    // member variables
    private int cPort;
    private int timeout;
    private Connection controllerConnection;
    private HeartbeatConnection controllerHeartbeat;
    private ClientInterface clientInterface;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public Client(int cPort, int timeout, ClientInterface clientInterface) {
        // initialising member variables
        this.cPort = cPort;
        this.timeout = timeout;
        this.clientInterface = clientInterface;
    }

    /**
     * Sets up and starts the Client for the system.
     * 
     * Trys to connect to the Controller.
     * 
     * Waits for user input if successful, closes otherwise.
     */
    public void start() throws Exception{
        try{
            // connecting to controller
            this.connectToController();
        }
        catch(Exception e){
            throw new Exception("Unable to connect Client to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     */
    private void connectToController() throws Exception{
        // setting up main connection
        this.controllerConnection = new Connection(InetAddress.getLocalHost(), this.cPort);
        this.controllerConnection.setSoTimeout(timeout);

        // setting up heartbeat connection
        Connection heartbeatConnection = new Connection(InetAddress.getLocalHost(), this.cPort);
        this.controllerHeartbeat = new HeartbeatConnection(this, heartbeatConnection);
        this.controllerHeartbeat.start();
    }

    /**
     * Send's a user's input request to the Controller the Client is connected to.
     */
    public void sendRequest(String request){
        try{
            // Sending request
            this.controllerConnection.getTextOut().println(request);
            this.controllerConnection.getTextOut().flush(); 

            // logging request
            this.clientInterface.logMessageSent(this.controllerConnection, request);

            // gathering response
            this.gatherResponse(request);
        }
        catch(Exception e){
            this.clientInterface.logError("Unable to send request : \"" + request + "\" to Controller on port : " + this.cPort);
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
            String response = this.controllerConnection.getTextIn().readLine();

            // response gathered within timeout...

            // handling response
            this.clientInterface.handleResponse(this.controllerConnection, response);
        }
        catch(SocketTimeoutException e){
            this.clientInterface.logError("Timeout occurred on request : \"" + request + "\" to Controller on port : " + this.cPort);
        }
        catch(Exception e){
            this.clientInterface.logError("Unable to recieve response for request : \"" + request + "\" from Controller on port : " + this.cPort + " (Controller likley disconnected).");
        }
    }

    /**
     * Handles the termination of the connection between the Client and the Controller
     */
    public void handleControllerDisconnect(){
        this.clientInterface.logError("Lost connection to Controller on port : " + this.cPort);

        // TODO The log error method in client terminal should handle the disconnect and allow the user to reconnect at some point
        // at the moment, it just closes the program.

        // closing the program (for now ...)
        System.exit(0);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ClientInterface getClientInterface(){
        return this.clientInterface;
    }
}
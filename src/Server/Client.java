package Server;

import java.net.InetAddress;
import java.net.SocketTimeoutException;

import Interface.ClientInterface;

/**
 * Abstract class to represent a Client within a Client-Server system.
 * 
 * When started, a Client will try to connect to the server, throwing an exception
 * if this was not possible.
 * 
 * If the Client was successfully started, requests can be sent to the Server using the  
 * 'sendRequest' method, and the underlying Client implementation will recieve the responses
 * to these requests through the 'handleResponse' method.
 */
public abstract class Client {

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
     * Trys to connect to the Server.
     * 
     * Waits for user input if successful, closes otherwise.
     */
    public void start() throws Exception{
        try{
            // connecting to server
            this.connectToServer();
        }
        catch(Exception e){
            throw new Exception("Unable to connect Client to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     */
    private void connectToServer() throws Exception{
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
     * 
     * All clients will send requests in the same way, so this method is implemented in the
     * abstract class.
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
            this.clientInterface.handleError("Unable to send request : \"" + request + "\" to Controller on port : " + this.cPort);
        }
    }

    /**
     * Gathers a response for a request.
     * 
     * Reads from the InputReader of the Server connection for the response.
     * 
     * @param request The request the response is being gathered for.
     */
    private void gatherResponse(String request){
        try{
            // FORMATTING
            System.out.println("Waiting for response...");

            // gathering response from controller 
            String response = this.controllerConnection.getTextIn().readLine();

            // response gathered within timeout...

            // handling response
            this.handleResponse(this.controllerConnection, response);
        }
        catch(SocketTimeoutException e){
            this.clientInterface.handleError("Timeout occurred on request : \"" + request + "\" to Controller on port : " + this.cPort);
        }
        catch(Exception e){
            this.clientInterface.handleError("Unable to recieve response for request : \"" + request + "\" from Controller on port : " + this.cPort + " (Controller likley disconnected).");
        }
    }

    /**
     * Handles the given response.
     * 
     * The underlying type of Client will need to implement the method to handle the recieved
     * message appropriatley. 
     * 
     * This could involve simply outputting the response to the screen, or sending a further request 
     * based on the recieved response.
     * 
     * Different types of client will handle responses in different ways, and so the specific type
     * of client must provide implementation for this method.
     * 
     * @param connection The socket the response was recieved from.
     * @param response The response message.
     */
    public abstract void handleResponse(Connection connection, String response);

    /**
     * Handles the termination of the connection between the Client and the Controller.
     * 
     * Onlly thing to do is log error and let the interface deal with it.
     */
    public void handleServerDisconnect(){
        // logging error
        this.clientInterface.handleError("Lost connection to Controller on port : " + this.cPort);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getCPort(){
        return this.cPort;
    }

    public ClientInterface getClientInterface(){
        return this.clientInterface;
    }
}
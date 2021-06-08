package Network;

import java.net.InetAddress;

import Interface.NetworkInterface;

/**
 * Abstract class to represent a Client within a Client-Server system.
 * 
 * When started, a Client will try to connect to the server, throwing an exception
 * if this was not possible.
 */
public abstract class Client implements NetworkProcess{

    // member variables
    private int cPort;
    private int timeout;
    private Connection serverConnection;
    private HeartbeatConnection serverHeartbeat;
    private NetworkInterface clientInterface;

    /**
     * Class Constructor.
     * 
     * @param cPort The port of the Controller.
     * @param timeout The message timeout period.
     */
    public Client(int cPort, int timeout, NetworkInterface clientInterface) {
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
            throw new Exception("Unable to connect Client to Server on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     */
    private void connectToServer() throws Exception{
        // setting up main connection
        this.serverConnection = new Connection(this.clientInterface, InetAddress.getLocalHost(), this.cPort);

        // setting up heartbeat connection
        Connection heartbeatConnection = new Connection(this.clientInterface, InetAddress.getLocalHost(), this.cPort);
        this.serverHeartbeat = new HeartbeatConnection(this, heartbeatConnection);
        this.serverHeartbeat.start();
    }

    /**
     * Handles the given input request from the user.
     * 
     * Different clients will handle requests in different ways, and so the underlying
     * Client will need to provide implementation for the method.
     * 
     * @param request The request provided by the user.
     */
    public abstract void handleInputRequest(String request);

    /**
     * Handles the termination of the connection between the Client and the Controller.
     * 
     * Onlly thing to do is log error and let the interface deal with it.
     */
    public void handleServerDisconnect(){
        // logging error
        this.clientInterface.handleError("Lost connection to Server on port : " + this.cPort);
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getCPort(){
        return this.cPort;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public Connection getServerConnection(){
        return this.serverConnection;
    }

    public NetworkInterface getClientInterface(){
        return this.clientInterface;
    }
}
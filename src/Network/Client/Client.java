package Network.Client;

import java.util.ArrayList;

import Network.Connection;
import Network.NetworkInterface;
import Network.NetworkProcess;
import Network.Protocol.Exception.ClientSetupException;
import Network.Protocol.Exception.ClientStartException;
import Network.Protocol.Exception.ConnectToServerException;
import Network.Server.Server;
import Network.Server.Server.ServerType;

/**
 * Abstract class to represent a Client within a Client-Server system.
 * 
 * When started, a Client will try to connect to the server, throwing an exception
 * if this was not possible.
 */
public abstract class Client implements NetworkProcess{

    // member variables
    private int serverPort;
    private int timeout;
    private NetworkInterface networkInterface;
    private Connection serverConnection;
    private HeartbeatConnection serverHeartbeat;
    private ArrayList<Connection> secondaryServerConnections;

    /**
     * Class Constructor.
     * 
     * @param serverPort The port of the Server.
     * @param timeout The message timeout period.
     * @param networkInterface The network interface for the client.
     */
    public Client(int serverPort, int timeout, NetworkInterface networkInterface) {
        // initialising member variables
        this.serverPort = serverPort;
        this.timeout = timeout;
        this.networkInterface = networkInterface;
        this.secondaryServerConnections = new ArrayList<Connection>();
    }

    /**
     * Sets up the Client ready for use.
     * 
     * @throws ClientStartException If the Client could not be setup.
     */
    public abstract void setup() throws ClientSetupException;

    /**
     * Sets up and starts the Client for the system.
     * 
     * @throws ClientStartException If the Client could not be started.
     */
    public void start() throws ClientStartException{
        try{
            // connecting to server
            this.connectToServer();

            // setting up the client
            this.setup();
        }
        catch(Exception e){
            throw new ClientStartException(e);
        }
    }

    /** 
     * Sets up a connection between the Client and the Controller.
     * 
     * @throws ConnectToServerException If the Client could not connnect to the Server.
     */
    private void connectToServer() throws ConnectToServerException{
        try{
            // setting up main connection
            this.serverConnection = new Connection(this.networkInterface, this.serverPort, ServerType.CONTROLLER);

            // setting up heartbeat connection
            Connection heartbeatConnection = new Connection(this.networkInterface, this.serverPort, ServerType.CONTROLLER);
            this.serverHeartbeat = new HeartbeatConnection(this, heartbeatConnection);
        }
        catch(Exception e){
            throw new ConnectToServerException(Server.ServerType.CONTROLLER, this.serverPort, e);
        }
    }
    
    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getServerPort(){
        return this.serverPort;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public NetworkInterface getNetworkInterface(){
        return this.networkInterface;
    }

    public Connection getServerConnection(){
        return this.serverConnection;
    }

    public HeartbeatConnection getServerHeartbeat(){
        return this.serverHeartbeat;
    }

    public ArrayList<Connection> getSecondaryServerConnections(){
        return this.secondaryServerConnections;
    }

    /////////////////
    // CLIENT TYPE //
    /////////////////

    /**
     * Enumeration class for the type of Client object that can exist within the 
     * system. 
     * 
     * These Client types are specific to the Distributed Data Store system.
     */
    public enum ClientType {
        // types
        CLIENT("Client"), // Client 
        DSTORE("Dstore"), // Dstore 
        CONTROLLER("Controller"), // Controller
        UNKNOWN("Unknown"); // Unknown client

        private String clientType;

        private ClientType(String clientType){
            this.clientType = clientType;
        }

        /**
         * Converts the client type method to a string.
         * @return String equivalent of the client type.
         */
        @Override
        public String toString(){
            return this.clientType;
        }
    }
}
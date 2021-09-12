package Network.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import Network.*;
import Network.Protocol.Exception.NewServerConnectionException;
import Network.Protocol.Exception.ServerSetupException;
import Network.Protocol.Exception.ServerWaitForConnectionException;
import Network.Protocol.Exception.ServerStartException;

/**
 * Abstract class to represent a Server object. 
 * 
 * Servers listen for new connectors and create new Connection objects to 
 * handle the request(s) the connector.
 */
public abstract class Server implements NetworkProcess{

    // member variables
    private ServerType type;
    private int port;
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;
    private NetworkInterface networkInterface;
    private volatile CopyOnWriteArrayList<Connection> clientConnections;
    private volatile ConcurrentHashMap<Connection, Integer> clientHeartbeatConnections;
    private volatile CopyOnWriteArrayList<Connection> serverConnections;
    private boolean active;

    /**
     * Class constructor
     * 
     * @param type The type of Server.
     * @param port The port the Server listens on.
     * @param serverInterface The network interface for the Server.
     */
    public Server(ServerType type, int port, NetworkInterface networkInterface){
        this.type = type;
        this.port = port;
        this.networkInterface = networkInterface;
        this.clientConnections = new CopyOnWriteArrayList<Connection>();
        this.clientHeartbeatConnections = new ConcurrentHashMap<Connection, Integer>() ;
        this.serverConnections = new CopyOnWriteArrayList<Connection>();
        this.active = true;
    }

    /**
     * Ran to setup the server and start waiting for connections.
     * 
     * @throws ServerStartException If the Server could not be started
     */
    public void start() throws ServerStartException{
        try{
            // running the server's setup method
            this.setup();

            // starting the server
            this.waitForConnection();
        }
        catch(Exception e){
            throw new ServerStartException(this.type, this.port, e);
        }
    }

    /**
     * Sets up the server before starting it.
     * 
     * Implemented by the Server instance to perform setup actions before the 
     * Server is started.
     * 
     * @throws ServerSetupException If the Server could not be setup.
     */
    public abstract void setup() throws ServerSetupException;


    /**
     * Makes the server start listening for incoming communication.
     * 
     * @throws ServerWaitForConnectionException If the server was unable to start waiting for
     * connections.
     */
    public void waitForConnection() throws ServerWaitForConnectionException{
        // Starting Listening //
        try{
            this.serverSocket = new ServerSocket(this.port);

            // listening for connections
            while (this.isActive()){
                try{
                    Socket socket = this.serverSocket.accept();
                    Connection connection = new Connection(this.getNetworkInterface(), socket);

                    // setting up the connection
                    this.setUpConnection(connection);
                }
                catch(Exception e){
                    this.handleError(new NewServerConnectionException(this.type, this.port, e));
                }
            }
        }
        catch(Exception e){
            throw new ServerWaitForConnectionException(this.type, this.port, e);
        }
    }

    /**
     * Sets up a connection between the Server and a connector.
     * 
     * @param connection The connection between the connector and the Server.
     */
    public void setUpConnection(Connection connection){
        // Setting up connnection to connector
        ServerThread serverThread = new ServerThread(this, connection);
        serverThread.start();
    }

    /**
     * Closes the server.
     */
    public void close(){
        this.active = false;
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public ServerType getType(){
        return this.type;
    }
    
    public RequestHandler getRequestHandler(){
        return this.requestHandler;
    }

    public NetworkInterface getNetworkInterface(){
        return this.networkInterface;
    }

    public CopyOnWriteArrayList<Connection> getClientConnections(){
        return this.clientConnections;
    }

    public ConcurrentHashMap<Connection, Integer> getClientHeartbeatConnections(){
        return this.clientHeartbeatConnections;
    }

    public CopyOnWriteArrayList<Connection> getServerConnections(){
        return this.serverConnections;
    }

    public boolean isActive(){
        return this.active;
    }

    public void setRequestHandler(RequestHandler requestHandler){
        this.requestHandler = requestHandler;
    }

    /////////////////
    // SERVER TYPE //
    /////////////////

    /**
     * Enumeration class for the type of Server object that can exist within the 
     * system.
     * 
     * These Server types are specific to the Distributed Storage System.
     */
    public enum ServerType {
        // types
        CONTROLLER("Controller"), // Controller sever
        DSTORE("Dstore"); // Dstore server

        private String serverType;

        private ServerType(String serverType){
            this.serverType = serverType;
        }

        /**
         * Converts the server type method to a string.
         * @return String equivalent of the server type.
         */
        @Override
        public String toString(){
            return this.serverType;
        }

        /**
         * Gathers the server type from the given string.
         * @param text The String form of the server type
         * @return The ServerType object for the server type.
         */
        public static ServerType fromString(String text) {
            for (ServerType type : ServerType.values()) {
                if (type.serverType.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return null;
        }
    }
}
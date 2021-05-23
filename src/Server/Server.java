package Server;

import java.net.ServerSocket;
import java.net.Socket;

import Interface.ServerInterface;

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
    private boolean active;
    private RequestHandler requestHandler;
    private ServerInterface serverInterface;

    /**
     * Class constructor
     */
    public Server(ServerType type, int port, ServerInterface serverInterface){
        this.type = type;
        this.port = port;
        this.serverInterface = serverInterface;
        this.active = true;
    }

    /**
     * Ran to setup the server and start waiting for connections.
     * 
     * Throws exception if the server could not be setup.
     */
    public void start() throws Exception{
        // running the server's setup method
        this.setup();

        // starting the server
        this.waitForConnection();
    }

    /**
     * Sets up the server before starting it.
     * 
     * Implemented by the Server instance to perform setup actions before the 
     * Server is started.
     */
    public abstract void setup() throws Exception;

    /**
     * Makes the server start listening for incoming communication.
     */
    public void waitForConnection(){
        // Starting Listening //
        try{
            this.serverSocket = new ServerSocket(this.port);

            // listening for connections
            while (this.isActive()){
                try{
                    Socket socket = this.serverSocket.accept();
                    Connection connection = new Connection(this.getServerInterface(), socket);

                    // setting up the connection
                    this.setUpConnection(connection);
                }
                catch(Exception e){
                    this.serverInterface.handleError(this.type.toString() + " on port : " + this.port + " unable to connect to new connector.");
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            this.serverInterface.handleError(this.type.toString() + " on port : " + this.port + " no longer running.");
        }
    }

    /**
     * Sets up a connection between the Server and a connector.
     * 
     * @param connection The connection between the connector and the Server.
     */
    public void setUpConnection(Connection connection){
        // Setting up connnection to connector
        try{
            ServerConnection serverConnection = new ServerConnection(this, connection);
            serverConnection.start();
        }
        catch(Exception e){
            this.serverInterface.handleError("Unable to create socket streams for connector on port : " + connection.getSocket().getPort());
        }
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * @param port The port of the connector.
     */
    public abstract void handleDisconnect(int port);

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

    public boolean isActive(){
        return this.active;
    }
    
    public RequestHandler getRequestHandler(){
        return this.requestHandler;
    }

    public ServerInterface getServerInterface(){
        return this.serverInterface;
    }

    public void setRequestHandler(RequestHandler requestHandler){
        this.requestHandler = requestHandler;
    }
}
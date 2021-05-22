package Server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Abstract class to represent a Server object. 
 * 
 * Servers listen for new connectors and create new Connection objects to 
 * handle the request(s) the connector.
 */
public abstract class Server {

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
     * Ran to set the server up to start recieving connections
     * 
     * Implemented by the Server instance (e.g., Controller)
     */
    public abstract void start() throws Exception;

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
                    Socket connection = this.serverSocket.accept();

                    // setting up the connection
                    this.setUpConnection(connection);
                }
                catch(Exception e){
                    this.serverInterface.logError(this.type.toString() + " on port : " + this.port + " unable to connect to new connector.");
                }
            }
        }
        catch(Exception e){
            this.serverInterface.logError(this.type.toString() + " on port : " + this.port + " down.");
        }
    }

    /**
     * Sets up a connection between the Server and a connector.
     * 
     * @param connection The connection between the connector and the Server.
     */
    public void setUpConnection(Socket connection){
        // Setting up connnection to connector
        try{
            ServerConnection serverConnection = new ServerConnection(this, connection);
            serverConnection.start();
        }
        catch(Exception e){
            this.serverInterface.logError("Unable to create socket streams for connector on port : " + connection.getPort());
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
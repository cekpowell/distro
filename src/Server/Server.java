package Server;

import java.net.ServerSocket;
import java.net.Socket;

import Logger.ControllerLogger;
import Logger.DstoreLogger;
import Logger.Logger;
import Logger.MyLogger;

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
    private Logger logger;

    /**
     * Class constructor
     */
    public Server(ServerType type, int port){
        this.type = type;
        this.port = port;
        this.active = true;
    }

    /**
     * Sets up the Server and starts it.
     * 
     * Setup immplemented by sub-class.
     * 
     * Start implemented by server.
     * 
     * @param requestHandler The request handler for the Server.
     */
    public void setupAndStart(RequestHandler requestHandler){
        /**
         * Performming the sub-class 'setup' method
         */
        this.setup();

        /**
         * Running the server.
         */
        this.start(requestHandler);
    }

    /**
     * Sets up the Server ready for use.
     * 
     * Implemented by the sub-class.
     */
    public abstract void setup();

    /**
     * Configures the Server and starts it.
     * 
     * Will:
     *      1 - Set the request handler of the server.
     *      2 - Set the logger for the server.
     *      3 - Runs the 'startListening()' method to starts listening for incoming 
     *          connections.
     * 
     * @param requestHandler The request handler for the Server.
     */
    private void start(RequestHandler requestHandler){
        // Setting Request Handler //

        this.setRequestHandler(requestHandler);

        try{
            // Setting Logger //

            if(this.type == ServerType.CONTROLLER){
                ControllerLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY);

                // assigning logger
                this.setLogger(ControllerLogger.getInstance());
            }
            else if(this.type == ServerType.DSTORE){
                DstoreLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY, this.port);

                // assigning logger
                this.setLogger(DstoreLogger.getInstance());
            }

            // Starting Listening //

            this.startListening();
        }
        catch(Exception e){
            MyLogger.logError("Unable to create" + this.type.toString() + " Logger for " + this.type.toString() + " on port : " + this.port);
        }
    }

    /**
     * Starts listening for incoming communication to the Server from a connector.
     */
    private void startListening(){
        try{
            this.serverSocket = new ServerSocket(this.port);

            // listening for connections
            while (this.isActive()){
                Socket connection = this.serverSocket.accept();

                // setting up the connection
                this.setUpConnection(connection);
            }
        }
        catch(Exception e){
            MyLogger.logError(this.type.toString() + " on port : " + this.port + " unable to connect to new connector.");
        }
    }

    /**
     * Sets up a connection between the Server and a connector.
     * 
     * @param connection The connection between the connector and the Server.
     */
    public void setUpConnection(Socket connection){
        // Setting up connnection to connector
        ServerConnection serverConnection = new ServerConnection(this, connection);
        serverConnection.start();
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

    public Logger getLogger(){
        return this.logger;
    }

    public void setRequestHandler(RequestHandler requestHandler){
        this.requestHandler = requestHandler;
    }

    public void setLogger(Logger logger){
        this.logger = logger;
    }
}
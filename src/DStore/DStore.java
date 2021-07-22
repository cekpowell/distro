package Dstore;

import java.io.File;
import java.net.InetAddress;
import java.util.StringTokenizer;

import Logger.*;
import Protocol.Exception.*;
import Protocol.Token.RequestTokenizer;
import Protocol.Token.Token;
import Protocol.Token.TokenType.ErrorDstorePortInUseToken;
import Protocol.Token.TokenType.JoinAckToken;
import Network.*;
import Network.Protocol.Exception.ClientDisconnectException;
import Network.Protocol.Exception.ConnectToServerException;
import Network.Protocol.Exception.ConnectionTerminatedException;
import Network.Protocol.Exception.HandeledNetworkException;
import Network.Protocol.Exception.NetworkException;
import Network.Protocol.Exception.ServerSetupException;
import Network.Server.Server;
import Network.Server.ServerThread;

 /**
  * Individual data store unit within the system. 
  *
  * Connects to a Controller to join a data store and servers requests from DSClients
  * and the Controller.
  */
public class Dstore extends Server{

    // member variables
    private int port;
    private int cPort;
    private int timeout;
    private String folderPath;
    private File fileStore;
    private ServerThread controllerThread;
    private ServerInterface networkInterface;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     * @param networkInterface The network interface for the Dstore.
     */
    public Dstore(int port, int cPort, int timeout, String folderPath, ServerInterface networkInterface){
        // initializing member variables
        super(ServerType.DSTORE, port, networkInterface);
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.folderPath = folderPath;
        this.networkInterface = networkInterface;
        this.setRequestHandler(new DstoreRequestHandler(this));
    }

    ///////////
    // SETUP //
    ///////////
    
    /**
     * Sets up the Dstore ready for use.
     * 
     * Creates the logger, connects to controller and creates file store.
     * 
     * @throws ServerSetupException If the Dstore could not be setup.
     */
    public void setup() throws ServerSetupException{
        try{
            // creating the terminal logger
            this.getServerInterface().createLogger();

            // connecting to controller
            this.connectToController();

            // setting up file storage folder
            this.setupFileStore(this.folderPath);
        }
        catch(Exception e){
            throw new ServerSetupException(ServerType.DSTORE, e);
        }
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     * 
     * @throws ConnectToServerException If the Dstore could not connect to the Controller.
     */
    public void connectToController() throws ConnectToServerException{
        try{
            // creating communicatoin channel
            Connection connection = new Connection(this.getServerInterface(), this.cPort);
            this.controllerThread = new ServerThread(this, connection);

            // sending JOIN message to Controller
            String message = Protocol.JOIN_TOKEN + " " + this.getPort();
            this.controllerThread.getConnection().sendMessage(message);

            // handling response from Controller

            Token response = RequestTokenizer.getToken(this.controllerThread.getConnection().getMessageWithinTimeout(this.timeout));

            if(response instanceof JoinAckToken){
                // Join Successful

                // starting the connection thread
                this.controllerThread.start();

                // TODO Log the joining as an event

            }
            else if(response instanceof ErrorDstorePortInUseToken){
                // Join not successful
                throw new DstorePortInUseException(this.port);
            }

        }
        catch(Exception e){
            throw new ConnectToServerException(ServerType.CONTROLLER, this.cPort, e);
        }
    }

    /**
     * Makes sure the DStores file store is ready to use by creating a directory
     * if one doesnt already exist.
     * 
     * @param folderPath The file store directory.
     */
    public void setupFileStore(String folderPath){
        // creating file object
        this.fileStore = new File(folderPath);

        // creating the file if it doesnt exist
        if(!this.fileStore.exists()){
            this.fileStore.mkdir();
        }
    }

    ////////////////////
    // ERROR HANDLING //
    ////////////////////

    /**
     * Handles an error that occured within the system.
     * 
     * @param error The error that has occured.
     */
    public void handleError(NetworkException error){
        // Connection Termination
        if(error instanceof ConnectionTerminatedException){
            ConnectionTerminatedException connection = (ConnectionTerminatedException) error;

            // Controller disconnected
            if(connection.getPort() == this.cPort){
                // logging disconnect
                this.getServerInterface().logError(new HandeledNetworkException(new ControllerDisconnectException(connection.getPort(), connection)));
            }
            // Client disconnected
            else{
                // logging disconnect
                this.getServerInterface().logError(new HandeledNetworkException(new ClientDisconnectException(connection.getPort(), connection)));
            }
        }
        // Non-important error - just need to log
        else{
            // logging error
            this.getServerInterface().logError(new HandeledNetworkException(error));
        }
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }

    public int getCPort(){
        return this.cPort;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public String getFolderPath(){
        return this.folderPath;
    }

    public File getFileStore(){
        return this.fileStore;
    }

    public ServerThread getControllerThread(){
        return this.controllerThread;
    }
}
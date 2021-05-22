package Dstore;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;

import Interface.ServerInterface;
import Logger.*;
import Server.*;

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
    private ServerConnection controllerConnection;
    private ServerInterface dstoreInterface;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     */
    public Dstore(int port, int cPort, int timeout, String folderPath, ServerInterface dstoreInterface){
        // initializing member variables
        super(ServerType.DSTORE, port, dstoreInterface);
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.folderPath = folderPath;
        this.dstoreInterface = dstoreInterface;
        this.setRequestHandler(new DstoreRequestHandler(this));
    }
    
    /**
     * Sets up the Dstore ready for use.
     * 
     * Creates the logger, connects to controller, creates file store and waits for 
     * connections.
     */
    public void start() throws Exception{
        try{
            // creating the terminal logger
            this.getServerInterface().createLogger();
        }
        catch(Exception e){
            throw new Exception("Unable to create Dstore Logger on port : " + this.getPort());
        }

        // connecting to controller
        try{
            // connecting to controller
            this.connectToController();

            // setting up file storage folder
            this.setupFileStore(this.folderPath);

            // Starting the server listening for connections.
            this.waitForConnection();
        }
        catch(Exception e){
            throw new Exception("Unable to connect DStore on port : " + this.port + " to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     */
    public void connectToController() throws Exception{
        // creating communicatoin channel
        Socket socket = new Socket(InetAddress.getLocalHost(), this.cPort);
        this.controllerConnection = new ServerConnection(this, socket);
        this.controllerConnection.start();

        // sending JOIN message to Controller
        String message = Protocol.JOIN_TOKEN + " " + this.getPort();
        this.controllerConnection.getTextOut().println(message);
        this.controllerConnection.getTextOut().flush();
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
        if(!fileStore.exists()){
            fileStore.mkdir();
        }
    }

    /**
     * Handles the disconnection of a Connector at the specified port.
     * 
     * Only thing to do is pass the error onto the underlying interface to handle.
     * 
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port){
        // logging disconnect
        this.getServerInterface().handleError("A connector on port : " + port + " has disconnected.");
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

    public File getFileStore(){
        return this.fileStore;
    }
}
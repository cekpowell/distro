package Dstore;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Logger.*;
import Server.*;

 /**
  * Individual data store unit within the system. Connects to a Controller to join a data store
  * and servers requests from clients.
  */
public class Dstore extends Server{

    // member variables
    private int port;
    private int cPort;
    private int timeout;
    private String folderPath;
    private File fileStore;
    private DstoreLogger logger;
    private ServerConnection controllerConnection;

    /**
     * Class constructor.
     * 
     * @param port The port the DStore will listen on.
     * @param cPort The port the Controller that the DStore will connect to is on.
     * @param timeout The timout period for the DStore.
     * @param fileFolder The folder where the DStore will store files.
     */
    public Dstore(int port, int cPort, int timeout, String folderPath){
        // initializing member variables
        super(ServerType.DSTORE,port);
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.folderPath = folderPath;

        // setting up and starting the server
        this.setupAndStart(new DstoreRequestHandler(this));
    }

    /**
     * Sets up the Dstore server ready for use.
     * 
     * Performs:
     *      1 - Connects the Dstore to the controller
     *      2 - Sets up the file store for the Dstore
     */
    public void setup(){
        // Trying to connect the DStore to the Controller //
        try{
            // connecting to controller
            this.connectToController();

            // Connection successful ...
        }
        catch(Exception e){
            MyLogger.logError("Unable to connect DStore on port : " + this.port + " to controller on port : " + this.cPort);
        }

        // setting up file storage folder
        this.setupFileStore(this.folderPath);
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
     * @param port The port of the connector.
     */
    public void handleDisconnect(int port){
        // Controller disconnected //

        if(this.cPort == port){
            MyLogger.logError("Controller on port : " + cPort + " has disconnected.");
            
            // closing active connections
            this.close();
            this.controllerConnection.close();

            // Stopping the program
            System.exit(0);
        }
        
        // Unknown connector
        MyLogger.logError("An unknown connector on port : " + port + " has disconnected.");
    }

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public int getPort(){
        return this.port;
    }

    public File getFileStore(){
        return this.fileStore;
    }

    /////////////////
    // MAIN METHOD //
    /////////////////

    /**
     * Main method - instantiates a new DStore instance using the command line parammeters.
     * 
     * @param args Parameters for the new DStore.
     */
    public static void main(String[] args){
        try{
            // gathering parameters
            int port = Integer.parseInt(args[0]);
            int cPort = Integer.parseInt(args[1]);
            int timeout = Integer.parseInt(args[2]);
            String fileFolder = args[3];

            // Creating new DStore instance
            Dstore dataStore = new Dstore(port, cPort, timeout, fileFolder);
        }
        catch(Exception e){
            MyLogger.logError("Unable to create DStore.");
        }
    }
}
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
    private Sender controllerSender;

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
        this.port = port;
        this.cPort = cPort;
        this.timeout = timeout;
        this.folderPath = folderPath;
        this.setRequestHandler(new DstoreRequestHandler(this));

        // starting the DStore
        this.setupAndRun();
    }

    /**
     * Sets up and starts the DStore for the system.
     * 
     * Trys to setup a Logger and connect to the Controller.
     * 
     * Set's up File Store and listens for connections if successful, closes otherwise.
     */
    public void setupAndRun(){
        // creating logger
        try{
            DstoreLogger.init(Logger.LoggingType.ON_TERMINAL_ONLY, this.port);
            this.setLogger(DstoreLogger.getInstance());
        }
        catch(Exception e){
            MyLogger.logError("Cannot create DStore Logger for DStore on port : " + this.port);
        }
        
        // Trying to connect the DStore to the Controller //
        try{
            // connecting to controller
            this.connectToController();

            // Connection successful ...

            // setting up file storage folder
            this.setupFileStore(this.folderPath);
            
            // waiting for client connection
            this.startListening();
        }
        catch(Exception e){
            MyLogger.logError("Unable to conect DStore on port : " + this.port + " to controller on port : " + this.cPort);
        }
    }

    /** 
     * Sets up a connection between the DStore and the Controller.
     */
    public void connectToController() throws Exception{
        // creating socket for communication
        Socket socket = new Socket(InetAddress.getLocalHost(), this.cPort);
            
        // creating communication channel
        this.controllerSender = new Sender(this, socket);

        // Sending JOIN request to controller
        String message = Protocol.JOIN_TOKEN + " " + this.getPort();
        this.controllerSender.sendMessage(message);
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
     * Handles incoming communication to the data store from a Client.
     */
    public void startListening(){
        try{
            ServerSocket listener = new ServerSocket(this.port);

            // listening for connections
            while (true){
                Socket connection = listener.accept();

                // setting up the connection
                this.setUpConnection(connection);
            }
        }
        catch(Exception e){
            MyLogger.logError("DStore on port : " + this.port + " unable to connect to new Client.");
        }
    }

    /**
     * Sets up a connection between the DStore and a Client.
     * 
     * @param connector The Client connecting the Dstore and the connector.
     * @throws Exception Thrown when connection could not be setup.
     */
    public void setUpConnection(Socket connection){
        // Setting up connnection to connector
        ServerConnection serverConnection = new ServerConnection(this, connection);
        serverConnection.start();
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